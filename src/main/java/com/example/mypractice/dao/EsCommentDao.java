package com.example.mypractice.dao;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.ElasticsearchException;
import co.elastic.clients.elasticsearch._types.SortOrder;
import co.elastic.clients.elasticsearch.core.*;
import co.elastic.clients.elasticsearch.core.search.Hit;
import com.example.mypractice.commons.constant.Others;
import com.example.mypractice.model.database.Comment;
import com.example.mypractice.model.database.Music;
import com.example.mypractice.model.database.User;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

@Repository
public class EsCommentDao {
    private static final String INDEX_NAME = "comment";
    @Autowired
    private ElasticsearchClient client;

    public int createComment(Comment comment) throws IOException, ElasticsearchException {
        IndexResponse response = client.index(indexRequestBuilder -> indexRequestBuilder
                .index(INDEX_NAME)
                .id(Long.toString(comment.getId()))
                .document(comment));
        Number success = response.shards().successful();
        if (success.intValue() > 0) {
            return 1;
        }
        return 0;
    }

    public List<Comment> selectCommentByUser(User user, Long lastIndex) throws IOException, ElasticsearchException {
        SearchResponse<Comment> response = null;
        if (lastIndex == null) {
            response = client.search(searchRequestBuilder -> searchRequestBuilder.index(INDEX_NAME)
                    .size(Others.PAGE_SIZE)
                    .sort(sortBuilder -> sortBuilder
                            .field(filedBuilder -> filedBuilder.field("id")))
                    .query(queryBuilder -> queryBuilder
                            .term(termBuilder -> termBuilder
                                    .value(Long.toString(user.getId())).field("author.id"))), Comment.class);
        } else {
            response = client.search(searchRequestBuilder -> searchRequestBuilder.index(INDEX_NAME)
                    .size(Others.PAGE_SIZE)
                    .sort(sortBuilder -> sortBuilder
                            .field(filedBuilder -> filedBuilder.field("id")))
                    .searchAfter(Long.toString(lastIndex))
                    .query(queryBuilder -> queryBuilder
                            .term(termBuilder -> termBuilder
                                    .value(Long.toString(user.getId())).field("author.id"))), Comment.class);
        }
        List<Comment> result = new LinkedList<>();
        for (Hit<Comment> hit : response.hits().hits()) {
            result.add(hit.source());
        }
        return result;
    }

    public Long selectCountByUser(User user) throws IOException, ElasticsearchException {
        CountResponse response = client.count(countRequestBuilder -> countRequestBuilder
                .index(INDEX_NAME)
                .query(queryBuilder -> queryBuilder
                        .term(termBuilder -> termBuilder
                                .value(Long.toString(user.getId()))
                                .field("author.id"))));
        return response.count();
    }

    public int deleteComment(Comment comment) throws IOException, ElasticsearchException {
        DeleteResponse response = client.delete(deleteRequestBuilder -> deleteRequestBuilder
                .index(INDEX_NAME)
                .id(Long.toString(comment.getId())));
        Number failed = response.shards().failed();
        if (failed.intValue() > 0) {
            return 0;
        }
        return 1;
    }

    public Comment selectCommentByCommentId(Comment comment) throws IOException, ElasticsearchException {
        GetResponse<Comment> response = client.get(getRequestBuilder -> getRequestBuilder
                .index(INDEX_NAME)
                .id(Long.toString(comment.getId())), Comment.class);
        return response.source();
    }

    public List<Comment> selectCommentByMusic(Music music, Long lastIndex) throws IOException, ElasticsearchException {
        SearchResponse<Comment> response = null;
        if (lastIndex == null) {
            response = client.search(searchRequestBuilder -> searchRequestBuilder.index(INDEX_NAME)
                    .size(Others.PAGE_SIZE)
                    .sort(sortBuilder -> sortBuilder
                            .field(filedBuilder -> filedBuilder.field("id")))
                    .query(queryBuilder -> queryBuilder
                            .term(termBuilder -> termBuilder
                                    .value(Long.toString(music.getId())).field("music.id"))), Comment.class);
        } else {
            response = client.search(searchRequestBuilder -> searchRequestBuilder.index(INDEX_NAME)
                    .size(Others.PAGE_SIZE)
                    .sort(sortBuilder -> sortBuilder
                            .field(filedBuilder -> filedBuilder.field("id")))
                    .searchAfter(Long.toString(lastIndex))
                    .query(queryBuilder -> queryBuilder
                            .term(termBuilder -> termBuilder
                                    .value(Long.toString(music.getId())).field("music.id"))), Comment.class);
        }
        List<Comment> result = new LinkedList<>();
        for (Hit<Comment> hit : response.hits().hits()) {
            result.add(hit.source());
        }
        return result;
    }

    public Long selectCountByMusic(Music music) throws IOException, ElasticsearchException {
        CountResponse response = client.count(countRequestBuilder -> countRequestBuilder
                .index(INDEX_NAME)
                .query(queryBuilder -> queryBuilder
                        .term(termBuilder -> termBuilder
                                .value(Long.toString(music.getId()))
                                .field("music.id"))));
        return response.count();
    }

    public List<Comment> selectCommentByContent(Comment comment, Long lastIndex, Double lastScore) throws IOException, ElasticsearchException {
        SearchResponse<Comment> response = null;
        if (lastIndex == null && lastScore == null) {
            response = client.search(searchRequestBuilder -> searchRequestBuilder
                    .index(INDEX_NAME)
                    .size(Others.PAGE_SIZE)
                    .sort(sortBuilder -> sortBuilder.score(scoreSortBuilder -> scoreSortBuilder.order(SortOrder.Desc)))
                    .sort(sortBuilder -> sortBuilder.field(fieldSortBuilder -> fieldSortBuilder.field("id")))
                    .query(queryBuilder -> queryBuilder.match(matchQueryBuilder -> matchQueryBuilder
                            .field("content").query(comment.getContent()))), Comment.class);
        } else {
            response = client.search(searchRequestBuilder -> searchRequestBuilder
                    .index(INDEX_NAME)
                    .size(Others.PAGE_SIZE)
                    .searchAfter("score", "id")
                    .sort(sortBuilder -> sortBuilder.score(scoreSortBuilder -> scoreSortBuilder.order(SortOrder.Desc)))
                    .sort(sortBuilder -> sortBuilder.field(fieldSortBuilder -> fieldSortBuilder.field("id")))
                    .query(queryBuilder -> queryBuilder.match(matchQueryBuilder -> matchQueryBuilder
                            .field("content").query(comment.getContent()))), Comment.class);
        }
        List<Comment> result = new LinkedList<>();
        for (Hit<Comment> hit : response.hits().hits()) {
            result.add(hit.source().setScore(hit.score()));
        }
        return result;
    }

    public Long selectCountByContent(Comment comment) throws IOException, ElasticsearchException {
        CountResponse response = client.count(countRequestBuilder -> countRequestBuilder
                .index(INDEX_NAME)
                .query(queryBuilder -> queryBuilder
                        .match(matchQueryBuilder -> matchQueryBuilder
                                .field("content").query(comment.getContent()))));
        return response.count();
    }

    /**
     * 当前的评论是否为该用户创建
     *
     * @param commentId
     * @param userId
     * @return 返回0，如果用户没有创建
     */
    public Long ifCreated(Long commentId, Long userId) throws IOException, ElasticsearchException {
        CountResponse response = client.count(countRequestBuilder -> countRequestBuilder
                .index(INDEX_NAME)
                .query(queryBuilder -> queryBuilder
                        .bool(boolQueryBuilder -> boolQueryBuilder
                                .must(mustQueryBuilder -> mustQueryBuilder
                                        .term(termQueryBuilder -> termQueryBuilder
                                                .field("id").value(commentId)))
                                .must(mustQueryBuilder -> mustQueryBuilder
                                        .term(termQueryBuilder -> termQueryBuilder
                                                .field("author.id").value(userId))))));
        return response.count();
    }
}
