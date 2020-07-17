/*
 * Copyright 2020 Amazon.com, Inc. or its affiliates. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License").
 * You may not use this file except in compliance with the License.
 * A copy of the License is located at
 *
 *  http://aws.amazon.com/apache2.0
 *
 * or in the "license" file accompanying this file. This file is distributed
 * on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied. See the License for the specific language governing
 * permissions and limitations under the License.
 */

package software.amazon.smithy.codegen.core;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

import software.amazon.smithy.build.FileManifest;
import software.amazon.smithy.build.MockManifest;
import software.amazon.smithy.model.node.Node;
import software.amazon.smithy.model.shapes.Shape;
import software.amazon.smithy.model.shapes.ShapeId;
import software.amazon.smithy.model.traits.AbstractTrait;
import software.amazon.smithy.model.traits.EnumTrait;

public class TraceProvider extends SymbolProviderDecorator {

    protected TraceFile.Builder traceFileBuilder;

    private boolean isMetadataFilled = false;
    private boolean isDefinitionsFilled = false;

    /**
     * Constructor for {@link SymbolProviderDecorator}.
     *
     * @param provider The {@link SymbolProvider} to be decorated.
     */
    public TraceProvider(SymbolProvider provider) {
        super(provider);
        traceFileBuilder = TraceFile.builder();
    }

    @Override
    public Symbol toSymbol(Shape shape) {
        Symbol symbol = provider.toSymbol(shape);

        if(!shape.getId().getNamespace().equals("smithy.api")) {

            addSymbolToShapes(symbol);

            //if metadata hasn't been filled yet
            if (!isMetadataFilled) {
                fillArtifactMetadata(symbol);
                isMetadataFilled = true;
            }

            //if our symbol has definitions defined
            if (!isDefinitionsFilled && symbol.getProperty(TraceFile.DEFINITIONS_TEXT).isPresent()) {
                traceFileBuilder.definitions(symbol.expectProperty(TraceFile.DEFINITIONS_TEXT, ArtifactDefinitions.class));
                isDefinitionsFilled = true;
            }

            try {
                buildAndWriteTraceFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return symbol;
    }

    public TraceFile getTraceFile() {
        return traceFileBuilder.build();
    }

    private void buildAndWriteTraceFile() throws IOException {
        TraceFile traceFile = traceFileBuilder.build();
        File file = new File("tracefile" + traceFile.getArtifactMetadata().getTimestamp());
        file.createNewFile();
        Writer writer = new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_8);
        PrintWriter pw = new PrintWriter(writer);
        pw.print(Node.prettyPrintJson(traceFile.toNode(), "  "));
        pw.close();
    }
    /**
     * Fills ArtifactMetadata with a type and timestamp based on symbol, and a temporary UUID for version and
     * id to be replaced once the version and id for the generated code are known.
     *
     * @param symbol Symbol to extract metadata from.
     */
    protected void fillArtifactMetadata(Symbol symbol) {
        //temporarily set id and version a UUID, this should be changed later once the version is known
        String tempIdVersion = UUID.randomUUID().toString();
        traceFileBuilder.artifact(ArtifactMetadata.builder()
                        .version(tempIdVersion)
                        .id(tempIdVersion)
                        .type(getTypeFromSymbol(symbol))
                        .setTimestampAsNow()
                        .build());
    }

    /**
     * Gets the language type for ArtifactMetadata from the file extension of the source file of a symbol.
     * Language specific code generators can override this.
     * @param symbol Symbol to extract type from.
     * @return String with the type of language.
     */
    protected String getTypeFromSymbol(Symbol symbol) {
        String type = "";
        String[] split = symbol.getDefinitionFile().split("\\.");
        String extension = split[split.length - 1].toLowerCase();
        switch (extension) {
            case "ts":
                type = "TypeScript";
                break;
            case "go":
                type = "Go";
                break;
            case "py":
                type = "Python";
                break;
            case "java":
                type = "Java";
                break;
            case "c":
                type = "C";
                break;
            case "rs":
                type = "Rust";
                break;
            default:
                type = "unrecognized type";
                break;
        }
        return type;
    }

    /**
     * Extracts a {@link ShapeId} from a {@link Symbol}, creates a {@link ShapeLink}, and adds them to
     * a {@link TraceFile}'s shapes map.
     *
     * @param symbol The {@link Symbol} to extract information from for the {@link TraceFile}'s shapes map.
     */
    protected void addSymbolToShapes(Symbol symbol) {
        //get the shape from our symbol
        ShapeId shapeId = symbol.expectProperty("shape", Shape.class).getId();

        //set ShapeLink's file
        ShapeLink link = ShapeLink.builder()
                .type(getShapeTypeFromSymbol(symbol))
                .id(getIdFromSymbol(symbol))
                .file(symbol.getDefinitionFile())
                .build();

        traceFileBuilder.addShapeLink(shapeId, link);
    }

    /**
     * Provides a default mapping from a {@link Symbol} to an Id.
     * Language specific code generators should override this.
     *
     * @param symbol {@link Symbol} to extract an id from.
     * @return String that contains the extracted id.
     */
    protected String getIdFromSymbol(Symbol symbol) {
        return symbol.toString()
                .replace(".", "")
                .replace(symbol.getNamespaceDelimiter(), ".");
    }

    /**
     * Provides a default mapping from a symbol to type. Language specific code generators should
     * override this.
     *
     * @param symbol The symbol to extract a type from.
     * @return String that contains the extracted type.
     */
    protected String getShapeTypeFromSymbol(Symbol symbol) {
        String type = "FIELD";
        for (Object object : symbol.getProperties().keySet()) {
            if (object.getClass().equals(EnumTrait.class)) {
                type = "ENUM";
            } else if (object.getClass().equals(Symbol.class)) {
                type = "METHOD";
            }
        }
        return type;
    }
}
