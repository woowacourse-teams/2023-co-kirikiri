package co.kirikiri.service.dto;

public record CustomPageRequest(
        int page,
        int size
) {

    private static final int PAGE_OFFSET = 1;

    public CustomPageRequest(final int page, final int size) {
        this.page = page - PAGE_OFFSET;
        this.size = size;
    }

    public int getOriginPage() {
        return this.page + PAGE_OFFSET;
    }
}
