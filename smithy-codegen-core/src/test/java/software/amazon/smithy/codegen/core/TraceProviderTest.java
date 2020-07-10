package software.amazon.smithy.codegen.core;

import org.junit.jupiter.api.Test;
import software.amazon.smithy.model.Model;
import software.amazon.smithy.model.shapes.Shape;
import software.amazon.smithy.model.shapes.ShapeId;

import java.io.IOException;
import java.net.URISyntaxException;

class TraceProviderTest {

    @Test
    void toSymbol() throws IOException, URISyntaxException {
        TraceProvider traceProvider = new TraceProvider(new SymbolProviderTestHelper());
        Model model = Model.assembler()
                .addImport(getClass().getResource("weather-service.smithy"))
                .assemble()
                .unwrap();
        for (Shape shape : model.toSet()) {
            traceProvider.toSymbol(shape);
        }
        //verifying that it can be written and parsed without exception
        traceProvider.getTraceFile().writeTraceFile(getClass().getResource("trace_file_output.txt").getFile());
        TraceFile traceFile = new TraceFile();
        traceFile.parseTraceFile(getClass().getResource("trace_file_output.txt").toURI());

        //verifying TraceFile contains the required fields
        assert(traceFile.getArtifactMetadata().getType().equals("TypeScript"));
        model.toSet().parallelStream().forEach(shape ->{
            ShapeId x = shape.getId();
            assert(traceFile.getShapes().containsKey(x));
            assert(traceFile.getShapes().get(x).get(0).getType().matches("ENUM|METHOD|FIELD"));
            assert(traceFile.getShapes().get(x).get(0).getFile().get().equals("namespace.ts"));
        });
    }

}