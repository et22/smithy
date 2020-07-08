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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import software.amazon.smithy.model.node.FromNode;
import software.amazon.smithy.model.node.Node;
import software.amazon.smithy.model.node.NodeMapper;
import software.amazon.smithy.model.node.ObjectNode;
import software.amazon.smithy.model.node.ToNode;

/**
 * Class that defines a link between the Smithy {@link software.amazon.smithy.model.shapes.Shape} and
 * the artifact that it produced.
 * <p>
 * ShapeLink objects contain the following information:
 * <ul>
 * <li> type - The type of artifact component. This value MUST correspond to one of the types defined
 * in the /definitions/types property of the trace file.</li>
 * <li> id - The artifact-specific identifier for the artifact component. For example, in Java a valid id
 * would be the fully-qualified name of a class, method, or field as defined in
 * <a href="https://docs.oracle.com/en/java/javase/11/docs/specs/doc-comment-spec.html"> Documentation Comment
 * Specification for the Standard Doclet</a></li>
 * <li> tags - Defines a list of tags to apply to the trace link. Each tag MUST correspond to a tag defined in
 * the /definitions/tags property of the trace file.</li>
 * <li> file - A URI that defines the location of the artifact component. Files MUST use the "file" URI scheme,
 * and SHOULD be relative.</li>
 * <li> line - The line number in the file that contains the artifact component.</li>
 * <li> column - The column number in the file that contains the artifact component.</li>
 * </ul>
 */
public class ShapeLink implements ToNode, FromNode, ValidateRequirements {
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
    private NodeMapper nodeMapper = new NodeMapper();

    public ShapeLink() {
    }

    private ShapeLink(String type, String id, List<String> tags, String file, Integer line, Integer column) {
        this.type = type;
        this.id = id;
        this.tags = tags;
        this.file = file;
        this.line = line;
        this.column = column;
    }

    /**
     * Instantiates ShapeLink instance variables by extracting data from an ObjectNode.
     *
     * @param jsonNode an ObjectNode that represents the a single ShapeLink
     */
    @Override
    public void fromNode(Node jsonNode) {
        ObjectNode node = jsonNode.expectObjectNode();

        type = nodeMapper.deserialize(node.expectStringMember(TYPE_TEXT), String.class);
        id = nodeMapper.deserialize(node.expectStringMember(ID_TEXT), String.class);

        if (node.containsMember(TAGS_TEXT)) {
            tags = nodeMapper.deserializeCollection(node.expectArrayMember(TAGS_TEXT), List.class, String.class);
        }
        if (node.containsMember(FILE_TEXT)) {
            file = nodeMapper.deserialize(node.expectStringMember(FILE_TEXT), String.class);
        }
        if (node.containsMember(LINE_TEXT)) {
            line = nodeMapper.deserialize(node.expectNumberMember(LINE_TEXT), Integer.class);
        }
        if (node.containsMember(COLUMN_TEXT)) {
            column = nodeMapper.deserialize(node.expectNumberMember(COLUMN_TEXT), Integer.class);
        }

        //error handling - required objects should not be null after parsing
        validateRequiredFields();
    }

    /**
     * Converts instance variables into an ObjectNode for writing out a ShapeLink.
     *
     * @return returns an ObjectNode that contains the StringNodes with the information
     * from a ShapeLink
     */
    @Override
    public ObjectNode toNode() {
        //error handling - required objects should not be null
        validateRequiredFields();

        Map<String, Object> toSerialize = new HashMap<>();

        toSerialize.put(ID_TEXT, id);
        toSerialize.put(TYPE_TEXT, type);
        if (tags != null) {
            toSerialize.put(TAGS_TEXT, tags);
        }
        if (file != null) {
            toSerialize.put(FILE_TEXT, file);
        }
        if (line != null) {
            toSerialize.put(LINE_TEXT, line);
        }
        if (column != null) {
            toSerialize.put(COLUMN_TEXT, column);
        }

        return nodeMapper.serialize(toSerialize).expectObjectNode();
    }

    /**
     * Checks if all of the ShapeLink's required fields are not null.
     *
     * @throws NullPointerException if any of the required fields are null
     */
    @Override
    public void validateRequiredFields() {
        Objects.requireNonNull(id);
        Objects.requireNonNull(type);
    }

    /**
     * Gets this ShapeLink's type.
     *
     * @return this ShapeLink's type
     */
    public String getType() {
        return type;
    }

    /**
     * Sets this ShapeLink's type.
     *
     * @param type represents type of this ShapeLink
     */
    public void setType(String type) {
        this.type = type;
    }

    /**
     * Gets this ShapeLink's id.
     *
     * @return this ShapeLink's id
     */
    public String getId() {
        return id;
    }

    /**
     * Sets this ShapeLink's Id.
     *
     * @param id represents id of this ShapeLink
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * Gets this ShapeLink's tags in an optional container.
     *
     * @return Optional container holding this ShapeLink's list of tags
     */
    public Optional<List<String>> getTags() {
        return Optional.ofNullable(tags);
    }

    /**
     * Sets this ShapeLink's tags list.
     *
     * @param tags a list of tags to assign to this ShapeLink
     */
    public void setTags(List<String> tags) {
        this.tags = tags;
    }

    /**
     * Gets this ShapeLink's file in an optional container.
     *
     * @return Optional container holding this ShapeLink's file
     */
    public Optional<String> getFile() {
        return Optional.ofNullable(file);
    }

    /**
     * Sets this ShapeLink's file.
     *
     * @param file represents file where this ShapeLink was generated from
     */
    public void setFile(String file) {
        this.file = file;
    }

    /**
     * Gets this ShapeLink's line number in an optional container.
     *
     * @return Optional container holding this ShapeLink's line number
     */
    public Optional<Integer> getLine() {
        return Optional.ofNullable(line);
    }

    /**
     * Sets this ShapeLink's line number.
     *
     * @param line represents link number of this ShapeLink
     */
    public void setLine(Integer line) {
        this.line = line;
    }

    /**
     * Gets this ShapeLink's column number in an optional container.
     *
     * @return Optional container holding this ShapeLink's column number
     */
    public Optional<Integer> getColumn() {
        return Optional.ofNullable(column);
    }

    /**
     * Sets this ShapeLink's column number.
     *
     * @param column represents column number of this ShapeLink
     */
    public void setColumn(Integer column) {
        this.column = column;
    }

    public static class ShapeLinkBuilder {

        private String type;
        private String id;
        private List<String> tags;
        private String file;
        private Integer line;
        private Integer column;

        /**
         * Constructs ShapeLink with required fields.
         *
         * @param type Type of ShapeLink.
         * @param id   Id of ShapeLink.
         */
        public ShapeLinkBuilder(String type, String id) {
            this.type = type;
            this.id = id;
        }

        /**
         * Sets tags list of a ShapeLink.
         *
         * @param tags list of tags.
         * @return This builder.
         */
        public ShapeLinkBuilder setTags(List<String> tags) {
            this.tags = tags;
            return this;
        }

        /**
         * Adds a tag to the tags list of a ShapeLink.
         *
         * @param tag tag to add.
         * @return This builder.
         */
        public ShapeLinkBuilder addTag(String tag) {
            if (tags == null) {
                tags = new ArrayList<>();
            }
            tags.add(tag);
            return this;
        }

        /**
         * Sets File of a ShapeLink.
         *
         * @param file File.
         * @return This builder.
         */
        public ShapeLinkBuilder setFile(String file) {
            this.file = file;
            return this;
        }

        /**
         * Sets line of a ShapeLink.
         *
         * @param line Line number in artifact file.
         * @return This builder.
         */
        public ShapeLinkBuilder setLine(Integer line) {
            this.line = line;
            return this;
        }

        /**
         * Sets tags list of a ShapeLink.
         *
         * @param column Column number in artifact file.
         * @return This builder.
         */
        public ShapeLinkBuilder setColumn(Integer column) {
            this.column = column;
            return this;
        }

        public ShapeLink build() {
            return new ShapeLink(type, id, tags, file, line, column);
        }
    }
}
