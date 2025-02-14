package dev.ebullient.convert.qute;

import java.nio.file.Path;

import io.quarkus.qute.TemplateData;
import io.quarkus.runtime.annotations.RegisterForReflection;

@TemplateData
@RegisterForReflection
public class ImageRef {
    final Path sourcePath;
    final Path targetFilePath;
    final Integer width;

    public final String title;
    public final String vaultPath;

    private ImageRef(Path sourcePath, Path targetFilePath, String title, String vaultPath, Integer width) {
        this.sourcePath = sourcePath;
        this.targetFilePath = targetFilePath;
        this.title = title == null ? "" : title;
        this.vaultPath = vaultPath;
        this.width = width;
    }

    public String getEmbeddedLink(String anchor) {
        return String.format("![%s](%s%s)", title, vaultPath,
                anchor.length() > 0 ? "#" + anchor : "");
    }

    public String getEmbeddedLink() {
        String anchor = "center";
        if (width != null && width < 500) {
            anchor = "right";
        }
        return String.format("![%s](%s#%s%s)",
                title.length() > 50 ? title.substring(0, 26) + "..." : title,
                vaultPath,
                anchor,
                title.length() > 50 ? " \"" + title + "\"" : "");
    }

    @Deprecated
    public String getEmbeddedLinkWithTitle(String anchor) {
        return getEmbeddedLink(anchor);
    }

    @Deprecated
    public String getCaption() {
        return title;
    }

    @Deprecated
    public String getPath() {
        return vaultPath;
    }

    /** Not available in templates */
    public Path sourcePath() {
        return sourcePath;
    }

    /** Not available in templates */
    public Path targetFilePath() {
        return targetFilePath;
    }

    public static class Builder {
        private Path sourcePath;
        private Path targetFilePath;
        private Path relativeTarget;
        private String title = "";
        private String vaultPath;
        private Integer width;

        private String vaultRoot;
        private Path rootFilePath;

        public Builder setSourcePath(Path sourcePath) {
            this.sourcePath = sourcePath;
            return this;
        }

        public Builder setStreamSource(String glyph) {
            this.sourcePath = Path.of("stream", glyph);
            return this;
        }

        public Builder setTitle(String title) {
            this.title = title.replaceAll("\\[(.+?)]\\(.+?\\)", "$1");
            return this;
        }

        public Builder setVaultRoot(String vaultRoot) {
            this.vaultRoot = vaultRoot;
            return this;
        }

        public Builder setRootFilepath(Path rootFilePath) {
            this.rootFilePath = rootFilePath;
            return this;
        }

        public Builder setRelativePath(Path relativeTarget) {
            this.relativeTarget = relativeTarget;
            return this;
        }

        public Builder setWidth(Integer width) {
            this.width = width;
            return this;
        }

        public ImageRef build() {
            if (sourcePath == null || relativeTarget == null || vaultRoot == null || rootFilePath == null) {
                throw new IllegalStateException("Set paths first (source, relative, vaultRoot, fileRoot) first");
            }
            this.targetFilePath = rootFilePath.resolve(relativeTarget);
            this.vaultPath = String.format("%s%s", vaultRoot,
                    relativeTarget.toString().replace('\\', '/'));

            return new ImageRef(sourcePath, targetFilePath, title, vaultPath, width);
        }

        public ImageRef build(ImageRef previous) {
            if (previous != null) {
                return new ImageRef(previous.sourcePath,
                        previous.targetFilePath,
                        title,
                        previous.vaultPath,
                        width);
            } else {
                return build();
            }
        }
    }
}
