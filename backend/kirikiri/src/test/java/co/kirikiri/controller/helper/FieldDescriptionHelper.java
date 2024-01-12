package co.kirikiri.controller.helper;

import org.springframework.restdocs.payload.FieldDescriptor;
import org.springframework.restdocs.snippet.Attributes;

import static co.kirikiri.controller.helper.RestDocsHelper.RESTRICT;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;

public class FieldDescriptionHelper {

    public static FieldDescriptor getDescriptor(final FieldDescription description) {
        if (description.getRestriction() == null) {
            return fieldWithPath(description.getPath()).description(description.getDescription());
        }
        return fieldWithPath(description.getPath()).description(description.getDescription())
                .attributes(new Attributes.Attribute(RESTRICT, description.getRestriction()));
    }

    public static class FieldDescription {

        private final String path;
        private final String description;
        private final String restriction;

        public FieldDescription(final String path, final String description) {
            this(path, description, null);
        }

        public FieldDescription(final String path, final String description, final String restriction) {
            this.path = path;
            this.description = description;
            this.restriction = restriction;
        }

        public String getPath() {
            return path;
        }

        public String getDescription() {
            return description;
        }

        public String getRestriction() {
            return restriction;
        }
    }
}
