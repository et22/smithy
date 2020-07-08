package software.amazon.smithy.codegen.core;

import org.junit.jupiter.api.Test;
import software.amazon.smithy.model.node.ObjectNode;

class ArtifactMetadataTest {

    @Test
    void toNode() {
        ArtifactMetadata am = new ArtifactMetadata();
        am.setId("a");
        am.setVersion("b");
        am.setType("c");
        am.setTimestamp("d");

        ObjectNode node = am.toNode();

        assert node.getStringMember(ArtifactMetadata.ID_TEXT).get().getValue().equals("a");
        assert node.getStringMember(ArtifactMetadata.VERSION_TEXT).get().getValue().equals("b");
        assert node.getStringMember(ArtifactMetadata.TYPE_TEXT).get().getValue().equals("c");
        assert node.getStringMember(ArtifactMetadata.TIMESTAMP_TEXT).get().getValue().equals("d");
    }

    @Test
    void fromNode() {
        ArtifactMetadata am = new ArtifactMetadata();
        am.setId("a");
        am.setVersion("b");
        am.setType("c");
        am.setTimestamp("d");

        ObjectNode node = am.toNode();

        ArtifactMetadata am2 = new ArtifactMetadata();
        am2.fromNode(node);

        assert am.getId().equals(am2.getId());
        assert am.getVersion().equals(am2.getVersion());
        assert am.getTimestamp().equals(am2.getTimestamp());
        assert am.getType().equals(am2.getType());
    }
}