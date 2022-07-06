package com.example.mypractice.dao;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.ElasticsearchException;
import co.elastic.clients.elasticsearch._types.SortOrder;
import co.elastic.clients.elasticsearch.core.*;
import co.elastic.clients.elasticsearch.core.bulk.BulkResponseItem;
import co.elastic.clients.elasticsearch.core.search.Hit;
import co.elastic.clients.json.JsonData;
import com.example.mypractice.commons.constant.Others;
import com.example.mypractice.model.database.Music;
import com.example.mypractice.model.database.MusicList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

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
     * 向自建歌单中添加歌曲，用脚本更新ES中的count和coverUrl
     *
     * @param musicList 　歌单信息
     * @return
     * @throws IOException
     * @throws ElasticsearchException
     */
    public int addMusicToList(MusicList musicList, String lastCoverUrl) throws IOException, ElasticsearchException {
        UpdateResponse<MusicList> response = client.update(updateRequestBuilder -> updateRequestBuilder
                .index(INDEX_NAME)
                .id(Long.toString(musicList.getId()))
                .script(scriptBuilder -> scriptBuilder
                        .inline(inlineScriptBuilder -> inlineScriptBuilder
                                .source("ctx._source.musicCount = ctx._source.musicCount + params.count;" +
                                        "ctx._source.coverUrl = params.coverUrl")
                                .params("count", JsonData.of(musicList.getMusicCount()))
                                .params("coverUrl", JsonData.of(lastCoverUrl)))), MusicList.class);
        Number success = response.shards().successful();
        if (success.intValue() >= 1) {
            return 1;
        }
        return 0;
    }

    /**
     * 从自建歌单中删除歌曲，用脚本更新ES中的count
     *
     * @param musicList 　歌单信息
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
     * 用painless脚本修改ES的字段，实现封禁
     *
     * @param musicList 　歌单信息
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
     * 用painless脚本修改ES的字段，实现解封
     *
     * @param musicList 　歌单信息
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
     * 用名称查询未被封禁的歌单
     *
     * @param musicList 歌单
     * @param lastIndex 上一次的id
     * @param lastScore 上一次的匹配分
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
                                    //使用bool查询，嵌套一个match和一个term作为两个条件
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
                                    //使用bool查询，嵌套一个match和一个term作为两个条件
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
     * 查询未被封禁的匹配名称的歌单的数量
     *
     * @param musicList 歌单信息
     * @return
     * @throws IOException
     * @throws ElasticsearchException
     */
    public Long selectCountByName(MusicList musicList) throws IOException, ElasticsearchException {
        CountResponse response = client.count(countRequestBuilder -> countRequestBuilder
                .index(INDEX_NAME)
                .query(queryBuilder -> queryBuilder
                        //使用bool查询，嵌套一个match和一个term作为两个条件
                        .bool(boolQueryBuilder -> boolQueryBuilder
                                .must(mustBuilder -> mustBuilder.match(matchBuilder -> matchBuilder
                                        .field("name").query(musicList.getName())))
                                .must(mustBuilder -> mustBuilder.term(termQueryBuilder -> termQueryBuilder
                                        .field("isBanned").value(MusicList.IN_USE))))));
        return response.count();
    }

    /**
     * 在用户添加到"我喜欢"后，修改歌单的收藏数
     *
     * @param musicId 歌单id
     * @param userId  用户id
     * @return
     * @throws IOException
     * @throws ElasticsearchException
     */
    public int addMusicListToLike(List<Long> musicId, Long userId) throws IOException, ElasticsearchException {
        BulkRequest.Builder bulkRequestBuilder = new BulkRequest.Builder();
        for (Long id : musicId) {
            bulkRequestBuilder.operations(operationBuilder -> operationBuilder
                    .update(updateOperationBuilder -> updateOperationBuilder
                            .id(Long.toString(id))
                            .index(INDEX_NAME)
                            .action(actionBuilder -> actionBuilder
                                    .script(scriptBuilder -> scriptBuilder
                                            .inline(inlineScriptBuilder -> inlineScriptBuilder
                                                    .source("ctx._source.likeCount = ctx._source.likeCount+params['count']")
                                                    .params("count", JsonData.of(1))
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
     * 在用户从"我喜欢"删除后，修改歌单的收藏数
     *
     * @param musicId 歌单id
     * @param userId  用户id
     * @return
     * @throws IOException
     * @throws ElasticsearchException
     */
    public int deleteMusicListFromLike(List<Long> musicId, Long userId) throws IOException, ElasticsearchException {
        BulkRequest.Builder bulkRequestBuilder = new BulkRequest.Builder();
        for (Long id : musicId) {
            bulkRequestBuilder.operations(operationBuilder -> operationBuilder
                    .update(updateOperationBuilder -> updateOperationBuilder
                            .id(Long.toString(id))
                            .index(INDEX_NAME)
                            .action(actionBuilder -> actionBuilder
                                    .script(scriptBuilder -> scriptBuilder
                                            .inline(inlineScriptBuilder -> inlineScriptBuilder
                                                    .source("ctx._source.likeCount = ctx._source.likeCount-params['count']")
                                                    .params("count", JsonData.of(1))
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
