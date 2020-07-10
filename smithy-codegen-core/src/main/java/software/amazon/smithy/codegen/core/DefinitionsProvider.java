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
import java.io.FileNotFoundException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Objects;

import software.amazon.smithy.model.shapes.Shape;

public class DefinitionsProvider extends SymbolProviderDecorator {
    public static final String DEFINITIONS_FILE_NAME = "definitions.txt";
    protected Definitions definitions = new Definitions();
    private boolean isDefinitionsFilled = false;

    /**
     * Constructor for {@link SymbolProviderDecorator}.
     *
     * @param provider The {@link SymbolProvider} to be decorated.
     */
    public DefinitionsProvider(SymbolProvider provider) {
        super(provider);
    }

    /**
     * Gets the symbol to define for the given shape.
     *
     * <p>A "symbol" represents the qualified name of a type in a target
     * programming language.
     *
     * <ul>
     *     <li>When given a structure, union, resource, or service shape,
     *     this method should provide the namespace and name of the type to
     *     generate.</li>
     *     <li>When given a simple type like a string, number, or timestamp,
     *     this method should return the language-specific type of the
     *     shape.</li>
     *     <li>When given a member shape, this method should return the
     *     language specific type to use as the target of the member.</li>
     *     <li>When given a list, set, or map, this method should return the
     *     language specific type to use for the shape (e.g., a map shape for
     *     a Python code generator might return "dict".</li>
     * </ul>
     *
     * @param shape Shape to get the class name of.
     * @return Returns the generated class name.
     */
    @Override
    public Symbol toSymbol(Shape shape) {
        Symbol symbol = provider.toSymbol(shape);
        if (!isDefinitionsFilled) {
            try {
                definitions.fromNode(definitions.fromDefinitionsFile(this.getClass().getResource(DEFINITIONS_FILE_NAME).toURI()));
                symbol = symbol.toBuilder().putProperty(TraceFile.DEFINITIONS_TEXT, definitions).build();
            } catch (URISyntaxException | FileNotFoundException e) {
                e.printStackTrace();
            }
        }
        return symbol;
    }
}
