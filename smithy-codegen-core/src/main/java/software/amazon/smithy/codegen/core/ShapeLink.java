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

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import software.amazon.smithy.model.node.Node;
import software.amazon.smithy.model.node.NodeMapper;
import software.amazon.smithy.model.node.ObjectNode;
import software.amazon.smithy.model.node.ToNode;
import software.amazon.smithy.utils.ListUtils;
import software.amazon.smithy.utils.SmithyBuilder;
import software.amazon.smithy.utils.ToSmithyBuilder;

/**
 * Class that defines a link between the Smithy {@link software.amazon.smithy.model.shapes.Shape} and
 * the artifact that it produced.
 */
public final class ShapeLink implements ToNode, ToSmithyBuilder<ShapeLink> {
    public static final String TYPE_TEXT = "type";
    public static final String ID_TEXT = "id";
    public static final String TAGS_TEXT = "tags";
    public static final String FILE_TEXT = "file";
    public static final String LINE_TEXT = "line";
    public static final String COLUMN_TEXT = "column";

    private String type;
    private String id;
    private List<String> tags; //optional
    private String file; //optional
    private Integer line; //optional
    private Integer column; //optional

    private ShapeLink(Builder builder) {
        type = SmithyBuilder.requiredState(TYPE_TEXT, builder.type);
        id = SmithyBuilder.requiredState(ID_TEXT, builder.id);
        tags = ListUtils.copyOf(builder.tags);
        file = builder.file;
        line = builder.line;
        column = builder.column;
    }

    /**
     * Instantiates ShapeLink instance variables by extracting data from an ObjectNode.
     *
     * @param value an ObjectNode that represents the a single ShapeLink
     */
    public static ShapeLink createFromNode(Node value) {
        return new NodeMapper().deserialize(value, ShapeLink.class);
    }

    public static Builder builder() {
        return new Builder();
    }

    /**
     * Converts instance variables into an ObjectNode for writing out a ShapeLink.
     *
     * @return returns an ObjectNode that contains the StringNodes with the information
     * from a ShapeLink
     */
    @Override
    public ObjectNode toNode() {
        return ObjectNode.objectNodeBuilder()
                .withMember(ID_TEXT, id)
                .withMember(TYPE_TEXT, type)
                .withOptionalMember(TAGS_TEXT, getTags().map(Node::fromStrings))
                .withOptionalMember(FILE_TEXT, getFile().map(Node::from))
                .withOptionalMember(LINE_TEXT, getLine().map(Node::from))
                .withOptionalMember(COLUMN_TEXT, getColumn().map(Node::from))
                .build();
    }

    /**
     * Gets this ShapeLink's type.
     * The type is the type of the artifact component. This value MUST correspond to one of the types defined
     * in the /definitions/types property of the trace file.
     *
     * @return this ShapeLink's type
     */
    public String getType() {
        return type;
    }

    /**
     * Gets this ShapeLink's id.
     * Id is the artifact-specific identifier for the artifact component. For example, in Java a valid id
     * would be the fully-qualified name of a class, method, or field as defined in
     * <a href="https://docs.oracle.com/en/java/javase/11/docs/specs/doc-comment-spec.html"> Documentation Comment
     * Specification for the Standard Doclet</a>
     *
     * @return this ShapeLink's id
     */
    public String getId() {
        return id;
    }

    /**
     * Gets this ShapeLink's tags in an optional container.
     * Tags defines a list of tags to apply to the trace link. Each tag MUST correspond to a tag defined in
     * the /definitions/tags property of the trace file.
     *
     * @return Optional container holding this ShapeLink's list of tags
     */
    public Optional<List<String>> getTags() {
        return Optional.ofNullable(tags);
    }

    /**
     * Gets this ShapeLink's file in an optional container.
     * File is a URI that defines the location of the artifact component. Files MUST use the "file" URI scheme,
     * and SHOULD be relative.
     *
     * @return Optional container holding this ShapeLink's file
     */
    public Optional<String> getFile() {
        return Optional.ofNullable(file);
    }

    /**
     * Gets this ShapeLink's line number in an optional container.
     * Line is the line number in the file that contains the artifact component.
     *
     * @return Optional container holding this ShapeLink's line number
     */
    public Optional<Integer> getLine() {
        return Optional.ofNullable(line);
    }

    /**
     * Gets this ShapeLink's column number in an optional container.
     * Column is the column number in the file that contains the artifact component.
     *
     * @return Optional container holding this ShapeLink's column number
     */
    public Optional<Integer> getColumn() {
        return Optional.ofNullable(column);
    }

    /**
     * Take this object and create a builder that contains all of the
     * current property values of this object.
     *
     * @return a builder for type T
     */
    @Override
    public SmithyBuilder<ShapeLink> toBuilder() {
        return builder()
                .id(id)
                .column(column)
                .type(type)
                .tags(tags)
                .line(line)
                .file(file);
    }

    public static final class Builder implements SmithyBuilder<ShapeLink> {
        private final List<String> tags = new ArrayList<>();
        private String type;
        private String id;
        private String file;
        private Integer line;
        private Integer column;

        public ShapeLink build() {
            return new ShapeLink(this);
        }

        public Builder type(String type) {
            this.type = type;
            return this;
        }

        public Builder id(String id) {
            this.id = id;
            return this;
        }

        /**
         * Sets tags list of a ShapeLink.
         *
         * @param tags list of tags.
         * @return This builder.
         */
        public Builder tags(List<String> tags) {
            this.tags.clear();
            this.tags.addAll(tags);
            return this;
        }

        /**
         * Adds a tag to the tags list of a ShapeLink.
         *
         * @param tag tag to add.
         * @return This builder.
         */
        public Builder addTag(String tag) {
            this.tags.add(tag);
            return this;
        }

        /**
         * Sets File of a ShapeLink.
         *
         * @param file File.
         * @return This builder.
         */
        public Builder file(String file) {
            this.file = file;
            return this;
        }

        /**
         * Sets line of a ShapeLink.
         *
         * @param line Line number in artifact file.
         * @return This builder.
         */
        public Builder line(Integer line) {
            this.line = line;
            return this;
        }

        /**
         * Sets tags list of a ShapeLink.
         *
         * @param column Column number in artifact file.
         * @return This builder.
         */
        public Builder column(Integer column) {
            this.column = column;
            return this;
        }

    }

}
