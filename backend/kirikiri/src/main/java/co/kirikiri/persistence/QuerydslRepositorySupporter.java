package co.kirikiri.persistence;

import com.querydsl.core.types.EntityPath;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.dsl.PathBuilder;
import com.querydsl.core.types.dsl.PathBuilderFactory;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.annotation.PostConstruct;
import jakarta.persistence.EntityManager;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.support.Querydsl;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;

@Repository
public abstract class QuerydslRepositorySupporter {

    private final PathBuilder<?> builder;
    private Querydsl querydsl;
    private EntityManager entityManager;
    private JPAQueryFactory queryFactory;

    public QuerydslRepositorySupporter(final Class<?> domainClass) {
        assert domainClass != null : "Domain class must not be null!";
        this.builder = new PathBuilderFactory().create(domainClass);
    }

    @Autowired
    public void setEntityManager(final EntityManager entityManager) {
        assert entityManager != null : "EntityManager must not be null!";
        this.entityManager = entityManager;
        this.querydsl = new Querydsl(entityManager, builder);
        this.queryFactory = new JPAQueryFactory(entityManager);
    }

    @PostConstruct
    public void validate() {
        assert entityManager != null : "EntityManager must not be null!";
        assert querydsl != null : "Querydsl must not be null!";
        assert queryFactory != null : "QueryFactory must not be null!";
    }

    protected <T> JPAQuery<T> select(final Expression<T> expr) {
        return getQueryFactory().select(expr);
    }

    protected <T> JPAQuery<T> selectFrom(final EntityPath<T> from) {
        return getQueryFactory().selectFrom(from);
    }

    protected <T> Page<T> applyPagination(final Pageable pageable,
                                          final JPAQuery<T> contentQuery,
                                          final JPAQuery<Long> countQuery) {
        final List<T> content = getQuerydsl().applyPagination(pageable, contentQuery).fetch();
        return PageableExecutionUtils.getPage(content, pageable, countQuery::fetchOne);
    }

    protected JPAQueryFactory getQueryFactory() {
        return queryFactory;
    }

    protected Querydsl getQuerydsl() {
        return querydsl;
    }
}
