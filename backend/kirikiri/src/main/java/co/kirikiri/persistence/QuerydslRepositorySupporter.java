package co.kirikiri.persistence;

import co.kirikiri.exception.ServerException;
import com.querydsl.core.types.EntityPath;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.dsl.PathBuilder;
import com.querydsl.core.types.dsl.PathBuilderFactory;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.annotation.PostConstruct;
import jakarta.persistence.EntityManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cglib.core.internal.Function;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.support.Querydsl;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public abstract class QuerydslRepositorySupporter {

    private final PathBuilder<?> builder;
    private Querydsl querydsl;
    private EntityManager entityManager;
    private JPAQueryFactory queryFactory;

    public QuerydslRepositorySupporter(final Class<?> domainClass) {
        if (domainClass == null) {
            throw new ServerException("Domain class must not be null!");
        }
        this.builder = new PathBuilderFactory().create(domainClass);
    }

    @Autowired
    public void setEntityManager(final EntityManager entityManager) {
        if (entityManager == null) {
            throw new ServerException("EntityManager must not be null!");
        }
        this.entityManager = entityManager;
        this.querydsl = new Querydsl(entityManager, builder);
        this.queryFactory = new JPAQueryFactory(entityManager);
    }

    @PostConstruct
    public void validate() {
        if (entityManager == null) {
            throw new ServerException("EntityManager must not be null!");
        }
        if (querydsl == null) {
            throw new ServerException("Querydsl must not be null!");
        }
        if (queryFactory == null) {
            throw new ServerException("QueryFactory must not be null!");
        }
    }

    protected <T> JPAQuery<T> select(final Expression<T> expr) {
        return getQueryFactory().select(expr);
    }

    protected <T> JPAQuery<T> selectFrom(final EntityPath<T> from) {
        return getQueryFactory().selectFrom(from);
    }

    protected <T> Page<T> applyPagination(final Pageable pageable,
                                          final Function<JPAQueryFactory, JPAQuery<T>> contentQuery,
                                          final Function<JPAQueryFactory, JPAQuery<Long>> countQuery) {
        final JPAQuery<T> jpaContentQuery = contentQuery.apply(getQueryFactory());
        final JPAQuery<Long> jpaCountQuery = countQuery.apply(getQueryFactory());
        final List<T> content = getQuerydsl().applyPagination(pageable, jpaContentQuery).fetch();
        return PageableExecutionUtils.getPage(content, pageable, jpaCountQuery::fetchOne);
    }

    protected JPAQueryFactory getQueryFactory() {
        return queryFactory;
    }

    protected Querydsl getQuerydsl() {
        return querydsl;
    }
}
