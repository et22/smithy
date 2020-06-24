package software.amazon.smithy.codegen.core;

import org.junit.jupiter.api.Test;
import software.amazon.smithy.model.shapes.ShapeId;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URISyntaxException;

class TraceFileTest {

    @Test
    void parseTraceFile() throws URISyntaxException, FileNotFoundException {
        TraceFile traceFile = new TraceFile();
        traceFile.parseTraceFile(getClass().getResource("tracefile.txt").toURI());

        assert traceFile.getArtifactMetadata().getId().equals("software.amazon.awssdk.services:snowball:2.10.79");
        assert traceFile.getDefinitions().get().getTags().containsValue("AWS SDK response builder");
        assert traceFile.getShapes().containsKey(ShapeId.from("com.amazonaws.snowball#Snowball"));
        assert traceFile.getSmithyTrace().equals("1.0");
    }

    @Test
    void writeTraceFile() throws URISyntaxException, IOException {
        TraceFile traceFile = new TraceFile();
        traceFile.parseTraceFile(getClass().getResource("tracefile.txt").toURI());
        traceFile.writeTraceFile(getClass().getResource("tracefile_output.txt").getFile());
        TraceFile traceFile2 = new TraceFile();
        traceFile2.parseTraceFile(getClass().getResource("tracefile_output.txt").toURI());

        assert traceFile2.getArtifactMetadata().getId().equals("software.amazon.awssdk.services:snowball:2.10.79");
        assert traceFile2.getDefinitions().get().getTags().containsValue("AWS SDK response builder");
        assert traceFile2.getShapes().containsKey(ShapeId.from("com.amazonaws.snowball#Snowball"));
        assert traceFile2.getSmithyTrace().equals("1.0");
    }
}