package co.kirikiri.service;

public enum ImageDirType {
    CHECK_FEED("goalroom/checkfeed"),
    ROADMAP_NODE("roadmap"),
    USER_PROFILE("member/profile");

    private final String dirName;

    ImageDirType(final String dirName) {
        this.dirName = dirName;
    }

    public String getDirName() {
        return dirName;
    }
}
