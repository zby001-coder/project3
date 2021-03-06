package com.example.mypractice.dao;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.ElasticsearchException;
import co.elastic.clients.elasticsearch._types.SortOrder;
import co.elastic.clients.elasticsearch.core.*;
import co.elastic.clients.elasticsearch.core.bulk.BulkResponseItem;
import co.elastic.clients.elasticsearch.core.search.Hit;
import co.elastic.clients.json.JsonData;
import com.example.mypractice.commons.constant.Others;
import com.example.mypractice.model.database.MusicList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

@Repository
public class EsMusicListDao {
    @Autowired
    private ElasticsearchClient client;
    @Autowired
    private EsMusicDao esMusicDao;
    private static final String INDEX_NAME = "music_list";

    public int addMusicList(MusicList musicList) throws IOException, ElasticsearchException {
        IndexResponse response = client.index(indexRequestBuilder -> indexRequestBuilder
                .index(INDEX_NAME)
                .id(Long.toString(musicList.getId()))
                .document(musicList));
        Number success = response.shards().successful();
        if (success.intValue() >= 1) {
            return 1;
        }
        return 0;
    }

    public int deleteMusicList(MusicList musicList) throws IOException, ElasticsearchException {
        DeleteResponse response = client.delete(deleteRequestBuilder -> deleteRequestBuilder
                .index(INDEX_NAME)
                .id(Long.toString(musicList.getId())));
        Number failed = response.shards().failed();
        if (failed.intValue() >= 1) {
            return 0;
        }
        return 1;
    }

    public int updateMusicList(MusicList musicList) throws IOException, ElasticsearchException {
        UpdateResponse<MusicList> response = client.update(updateRequestBuilder -> updateRequestBuilder
                .index(INDEX_NAME)
                .id(Long.toString(musicList.getId()))
                .doc(musicList), MusicList.class);
        Number success = response.shards().successful();
        if (success.intValue() >= 1) {
            return 1;
        }
        return 0;
    }

    /**
     * ?????????????????????url
     *
     * @param urls ?????????id?????????url???map
     * @return
     * @throws IOException
     * @throws ElasticsearchException
     */
    public void updateCoverUrl(Map<Object, Object> urls) throws IOException, ElasticsearchException {
        BulkRequest.Builder bulkRequestBuilder = new BulkRequest.Builder();
        for (Map.Entry<Object, Object> entry : urls.entrySet()) {
            bulkRequestBuilder.operations(operationBuilder -> operationBuilder
                    .update(updateOperationBuilder -> updateOperationBuilder
                            .index(INDEX_NAME)
                            .id(Long.toString((Long) entry.getKey()))
                            .action(actionBuilder -> actionBuilder
                                    .script(scriptBuilder -> scriptBuilder
                                            .inline(inlineScript -> inlineScript
                                                    .source("ctx._source.coverUrl = params.coverUrl")
                                                    .params("coverUrl", JsonData.of(entry.getValue())))))));
        }
        client.bulk(bulkRequestBuilder.build());
    }

    /**
     * ????????????????????????????????????????????????ES??????count
     *
     * @param musicList ???????????????
     * @return
     * @throws IOException
     * @throws ElasticsearchException
     */
    public int deleteMusicFromList(MusicList musicList) throws IOException, ElasticsearchException {
        UpdateResponse<MusicList> response = client.update(updateRequestBuilder -> updateRequestBuilder
                .index(INDEX_NAME)
                .id(Long.toString(musicList.getId()))
                .script(scriptBuilder -> scriptBuilder
                        .inline(inlineScriptBuilder -> inlineScriptBuilder
                                .source("ctx._source.musicCount = ctx._source.musicCount - params.count")
                                .params("count", JsonData.of(musicList.getMusicCount())))), MusicList.class);
        Number success = response.shards().successful();
        if (success.intValue() >= 1) {
            return 1;
        }
        return 0;
    }

    /**
     * ???painless????????????ES????????????????????????
     *
     * @param musicList ???????????????
     * @return
     * @throws IOException
     * @throws ElasticsearchException
     */
    public int banMusicList(MusicList musicList) throws IOException, ElasticsearchException {
        UpdateResponse<MusicList> response = client.update(updateRequestBuilder -> updateRequestBuilder
                .index(INDEX_NAME)
                .id(Long.toString(musicList.getId()))
                .script(scriptBuilder -> scriptBuilder
                        .inline(inlineScriptBuilder -> inlineScriptBuilder
                                .source("ctx._source.isBanned = params['state']")
                                .params("state", JsonData.of(MusicList.BANNED)))), MusicList.class);
        Number success = response.shards().successful();
        if (success.intValue() >= 1) {
            return 1;
        }
        return 0;
    }

    /**
     * ???painless????????????ES????????????????????????
     *
     * @param musicList ???????????????
     * @return
     * @throws IOException
     * @throws ElasticsearchException
     */
    public int unsealMusicList(MusicList musicList) throws IOException, ElasticsearchException {
        UpdateResponse<MusicList> response = client.update(updateRequestBuilder -> updateRequestBuilder
                .index(INDEX_NAME)
                .id(Long.toString(musicList.getId()))
                .script(scriptBuilder -> scriptBuilder
                        .inline(inlineScriptBuilder -> inlineScriptBuilder
                                .source("ctx._source.isBanned = params['state']")
                                .params("state", JsonData.of(MusicList.IN_USE)))), MusicList.class);
        Number success = response.shards().successful();
        if (success.intValue() >= 1) {
            return 1;
        }
        return 0;
    }

    /**
     * ????????????????????????????????????
     *
     * @param musicList ??????
     * @param lastIndex ????????????id
     * @param lastScore ?????????????????????
     * @return
     * @throws IOException
     * @throws ElasticsearchException
     */
    public List<MusicList> selectMusicListByName(MusicList musicList, Long lastIndex, Double lastScore) throws IOException, ElasticsearchException {
        SearchResponse<MusicList> response;
        if (lastIndex == null && lastScore == null) {
            response = client.search(searchRequestBuilder -> searchRequestBuilder
                            .index(INDEX_NAME)
                            .size(Others.PAGE_SIZE)
                            .sort(sortBuilder -> sortBuilder.score(scoreBuilder -> scoreBuilder.order(SortOrder.Desc)))
                            .sort(sortBuilder -> sortBuilder.field(fieldBuilder -> fieldBuilder.field("id")))
                            .query(queryBuilder -> queryBuilder
                                    //??????bool?????????????????????match?????????term??????????????????
                                    .bool(boolQueryBuilder -> boolQueryBuilder
                                            .must(mustBuilder -> mustBuilder.match(matchBuilder -> matchBuilder
                                                    .field("name").query(musicList.getName())))
                                            .must(mustBuilder -> mustBuilder.term(termQueryBuilder -> termQueryBuilder
                                                    .field("isBanned").value(MusicList.IN_USE)))))
                    , MusicList.class);
        } else {
            response = client.search(searchRequestBuilder -> searchRequestBuilder
                            .index(INDEX_NAME)
                            .size(Others.PAGE_SIZE)
                            .sort(sortBuilder -> sortBuilder.score(scoreBuilder -> scoreBuilder.order(SortOrder.Desc)))
                            .sort(sortBuilder -> sortBuilder.field(fieldBuilder -> fieldBuilder.field("id")))
                            .searchAfter(Double.toString(lastScore), Long.toString(lastIndex))
                            .query(queryBuilder -> queryBuilder
                                    //??????bool?????????????????????match?????????term??????????????????
                                    .bool(boolQueryBuilder -> boolQueryBuilder
                                            .must(mustBuilder -> mustBuilder.match(matchBuilder -> matchBuilder
                                                    .field("name").query(musicList.getName())))
                                            .must(mustBuilder -> mustBuilder.term(termQueryBuilder -> termQueryBuilder
                                                    .field("isBanned").value(MusicList.IN_USE)))))
                    , MusicList.class);
        }
        List<Hit<MusicList>> hits = response.hits().hits();
        List<MusicList> result = new LinkedList<>();
        for (Hit<MusicList> hit : hits) {
            result.add(hit.source().setScore(hit.score()));
        }
        return result;
    }

    /**
     * ???????????????????????????????????????????????????
     *
     * @param musicList ????????????
     * @return
     * @throws IOException
     * @throws ElasticsearchException
     */
    public Long selectCountByName(MusicList musicList) throws IOException, ElasticsearchException {
        CountResponse response = client.count(countRequestBuilder -> countRequestBuilder
                .index(INDEX_NAME)
                .query(queryBuilder -> queryBuilder
                        //??????bool?????????????????????match?????????term??????????????????
                        .bool(boolQueryBuilder -> boolQueryBuilder
                                .must(mustBuilder -> mustBuilder.match(matchBuilder -> matchBuilder
                                        .field("name").query(musicList.getName())))
                                .must(mustBuilder -> mustBuilder.term(termQueryBuilder -> termQueryBuilder
                                        .field("isBanned").value(MusicList.IN_USE))))));
        return response.count();
    }

    /**
     * ??????????????????"?????????"??????????????????????????????
     *
     * @param counts ??????list???id???likeCount????????????map
     */
    public int updateLikeCount(Map<Object, Object> counts) throws IOException, ElasticsearchException {
        BulkRequest.Builder bulkRequestBuilder = new BulkRequest.Builder();
        for (Map.Entry<Object, Object> entry : counts.entrySet()) {
            bulkRequestBuilder.operations(operationBuilder -> operationBuilder
                    .update(updateOperationBuilder -> updateOperationBuilder
                            .id(Long.toString((Long) entry.getKey()))
                            .index(INDEX_NAME)
                            .action(actionBuilder -> actionBuilder
                                    .script(scriptBuilder -> scriptBuilder
                                            .inline(inlineScriptBuilder -> inlineScriptBuilder
                                                    .source("ctx._source.likeCount = ctx._source.likeCount+params['count']")
                                                    .params("count", JsonData.of(entry.getValue()))
                                            )))));
        }
        BulkResponse response = client.bulk(bulkRequestBuilder.build());
        for (BulkResponseItem item : response.items()) {
            Number successful = item.shards().successful();
            if (successful.intValue() < 1) {
                return 0;
            }
        }
        return 1;
    }

    /**
     * ????????????????????????
     *
     * @param counts ???????????????id???map
     * @return
     */
    int updateMusicCount(Map<Object, Object> counts) throws IOException, ElasticsearchException {
        BulkRequest.Builder bulkRequestBuilder = new BulkRequest.Builder();
        for (Map.Entry<Object, Object> entry : counts.entrySet()) {
            bulkRequestBuilder.operations(operationBuilder -> operationBuilder
                    .update(updateOperationBuilder -> updateOperationBuilder
                            .id(Long.toString((Long) entry.getKey()))
                            .index(INDEX_NAME)
                            .action(actionBuilder -> actionBuilder
                                    .script(scriptBuilder -> scriptBuilder
                                            .inline(inlineScriptBuilder -> inlineScriptBuilder
                                                    .source("ctx._source.musicCount = ctx._source.musicCount+params['count']")
                                                    .params("count", JsonData.of(entry.getValue()))
                                            )))));
        }
        BulkResponse response = client.bulk(bulkRequestBuilder.build());
        for (BulkResponseItem item : response.items()) {
            Number successful = item.shards().successful();
            if (successful.intValue() < 1) {
                return 0;
            }
        }
        return 1;
    }
}
