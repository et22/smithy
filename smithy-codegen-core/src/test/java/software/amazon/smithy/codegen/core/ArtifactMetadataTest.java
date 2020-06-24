package software.amazon.smithy.codegen.core;

import org.junit.jupiter.api.Test;
import software.amazon.smithy.model.node.ObjectNode;

class ArtifactMetadataTest {

    @Test
    void toJsonNode() {
        ArtifactMetadata am = new ArtifactMetadata();
        am.setId("a");
        am.setVersion("b");
        am.setType("c");
        am.setTimestamp("d");

        ObjectNode node = am.toJsonNode();

        assert node.getStringMember(am.idText).get().getValue().equals("a");
        assert node.getStringMember(am.versionText).get().getValue().equals("b");
        assert node.getStringMember(am.typeText).get().getValue().equals("c");
        assert node.getStringMember(am.timestampText).get().getValue().equals("d");
    }

    @Test
    void fromJsonNode() {
        ArtifactMetadata am = new ArtifactMetadata();
        am.setId("a");
        am.setVersion("b");
        am.setType("c");
        am.setTimestamp("d");

        ObjectNode node = am.toJsonNode();

        ArtifactMetadata am2 = new ArtifactMetadata();
        am2.fromJsonNode(node);

        assert am.getId().equals(am2.getId());
        assert am.getVersion().equals(am2.getVersion());
        assert am.getTimestamp().equals(am2.getTimestamp());
        assert am.getType().equals(am2.getType());
    }
}