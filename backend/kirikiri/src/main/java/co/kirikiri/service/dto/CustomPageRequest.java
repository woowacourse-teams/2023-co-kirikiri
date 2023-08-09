package co.kirikiri.service.dto;

// TODO 페이징용 객체. 무한스크롤 적용이 클라이언트에서 완전히 적용되면 제거 예정
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
