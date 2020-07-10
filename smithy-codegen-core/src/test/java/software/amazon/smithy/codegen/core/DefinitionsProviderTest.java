package software.amazon.smithy.codegen.core;

import org.junit.jupiter.api.Test;
import software.amazon.smithy.model.Model;
import software.amazon.smithy.model.shapes.Shape;

import java.io.IOException;
import java.net.URISyntaxException;

import static org.junit.jupiter.api.Assertions.*;

class DefinitionsProviderTest {
    @Test
    void toSymbol() throws IOException, URISyntaxException {
        TraceProvider traceProvider = new TraceProvider(new DefinitionsProvider(new SymbolProviderTestHelper()));
        Model model = Model.assembler()
                .addImport(getClass().getResource("weather-service.smithy"))
                .assemble()
                .unwrap();
        for (Shape shape : model.toSet()) {
            traceProvider.toSymbol(shape);
        }
        //verifying that it can be written and parsed without exceptions
        traceProvider.getTraceFile().writeTraceFile(getClass().getResource("trace_file_output.txt").getFile());
        TraceFile traceFile = new TraceFile();
        traceFile.parseTraceFile(getClass().getResource("trace_file_output.txt").toURI());

        assert traceFile.getDefinitions().get().getTypes().containsKey("t1");

        assert traceFile.getDefinitions().get().getTypes().containsValue("t1val");

        assert traceFile.getDefinitions().get().getTypes().containsKey("t2");

        assert traceFile.getDefinitions().get().getTypes().containsValue("t2val");

        assert traceFile.getDefinitions().get().getTags().containsKey("tag2");

        assert traceFile.getDefinitions().get().getTags().containsValue("tag2val");

        assert traceFile.getDefinitions().get().getTags().containsKey("tag1");

        assert traceFile.getDefinitions().get().getTags().containsValue("tag1val");
    }
}