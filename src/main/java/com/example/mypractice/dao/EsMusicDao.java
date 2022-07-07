package com.example.mypractice.dao;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.ElasticsearchException;
import co.elastic.clients.elasticsearch._types.SortOrder;
import co.elastic.clients.elasticsearch.core.*;
import co.elastic.clients.elasticsearch.core.bulk.BulkResponseItem;
import co.elastic.clients.elasticsearch.core.mget.MultiGetResponseItem;
import co.elastic.clients.elasticsearch.core.search.Hit;
import co.elastic.clients.json.JsonData;
import com.example.mypractice.commons.constant.Others;
import com.example.mypractice.model.database.Music;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * 和ES中的MusicIndex交互的工具
 *
 * @author 张贝易
 */
@Repository
public class EsMusicDao {
    @Autowired
    private ElasticsearchClient client;
    private static final String INDEX_NAME = "music";

    /**
     * 添加一个Music到Index中
     *
     * @param music 歌曲内容
     * @return 返回1表示成功，返回0表示失败
     * @throws IOException
     * @throws ElasticsearchException
     */
    public int addMusic(Music music) throws IOException, ElasticsearchException {
        IndexResponse response = client.index(indexRequestBuilder -> indexRequestBuilder
                .index(INDEX_NAME)
                .id(Long.toString(music.getId()))
                .document(music));
        Number success = response.shards().successful();
        if (success.intValue() >= 1) {
            return 1;
        }
        return 0;
    }

    /**
     * 从Index中删除一个Music
     *
     * @param music music信息
     * @return 返回1表示删除成功，返回0表示删除失败
     * @throws IOException
     * @throws ElasticsearchException
     */
    public int deleteMusic(Music music) throws IOException, ElasticsearchException {
        DeleteResponse response = client.delete(deleteRequestBuilder -> deleteRequestBuilder
                .index(INDEX_NAME)
                .id(Long.toString(music.getId())));
        Number failed = response.shards().failed();
        if (failed.intValue() >= 1) {
            return 0;
        }
        return 1;
    }

    /**
     * 在Index中更新一个Music
     *
     * @param music music信息
     * @return 返回1表示更新成功，返回0表示更新失败
     * @throws IOException
     * @throws ElasticsearchException
     */
    public int updateMusic(Music music) throws IOException, ElasticsearchException {
        UpdateResponse<Music> response = client.update(updateRequestBuilder -> updateRequestBuilder
                .index(INDEX_NAME)
                .id(Long.toString(music.getId()))
                .doc(music), Music.class);
        Number success = response.shards().successful();
        if (success.intValue() >= 1) {
            return 1;
        }
        return 0;
    }

    /**
     * 用音乐的id查询音乐
     *
     * @param music 音乐信息
     * @return 查询结果
     * @throws IOException
     * @throws ElasticsearchException
     */
    public Music selectMusicById(Music music) throws IOException, ElasticsearchException {
        GetResponse<Music> response = client.get(getRequestBuilder -> getRequestBuilder
                .index(INDEX_NAME)
                .id(Long.toString(music.getId())), Music.class);
        return response.source();
    }

    /**
     * 批量查询歌曲
     *
     * @param musicList 歌曲信息
     * @return
     */
    public List<Music> selectMusicByIdBulk(List<Music> musicList) throws IOException, ElasticsearchException {
        List<String> ids = new LinkedList<>();
        for (Music music : musicList) {
            ids.add(Long.toString(music.getId()));
        }
        MgetResponse<Music> response = client.mget(requestBuilder -> requestBuilder
                .index(INDEX_NAME)
                .ids(ids)
                .sourceIncludes("id", "musicName", "singerName"), Music.class);
        List<Music> result = new LinkedList<>();
        System.out.println(response.docs());
        for (MultiGetResponseItem<Music> doc : response.docs()) {
            result.add(doc.result().source());
        }
        return result;
    }

    /**
     * 用音乐名称模糊查询音乐信息，只能一页页向下查询，用匹配分数和ID排序，是全文匹配
     *
     * @param music     音乐信息
     * @param lastIndex 上一次查询的最后一个Id
     * @param lastScore 上一次查询的最后一个分数
     * @return 音乐信息
     * @throws IOException
     * @throws ElasticsearchException
     */
    public List<Music> selectMusicByName(Music music, Long lastIndex, Double lastScore) throws IOException, ElasticsearchException {
        SearchResponse<Music> response;
        if (lastIndex != null && lastScore != null) {
            final String lastIndex1 = Long.toString(lastIndex);
            final String lastScore1 = Double.toString(lastScore);
            response = client.search(searchRequestBuilder -> searchRequestBuilder
                            .index(INDEX_NAME).size(Others.PAGE_SIZE)
                            .sort(sortBuilder -> sortBuilder.score(scoreBuilder -> scoreBuilder.order(SortOrder.Desc)))
                            .sort(sortBuilder -> sortBuilder.field(filedBuilder -> filedBuilder.field("id")))
                            .searchAfter(lastScore1, lastIndex1)
                            .fields(fieldBuilder -> fieldBuilder.field("id").field("name").field("singerName"))
                            .query(queryBuilder -> queryBuilder
                                    .match(matchBuilder -> matchBuilder.field("musicName").query(music.getMusicName())))
                    , Music.class);
        } else {
            response = client.search(searchRequestBuilder -> searchRequestBuilder
                            .index(INDEX_NAME).size(Others.PAGE_SIZE)
                            .sort(sortBuilder -> sortBuilder.score(scoreBuilder -> scoreBuilder.order(SortOrder.Desc)))
                            .sort(sortBuilder -> sortBuilder.field(filedBuilder -> filedBuilder.field("id")))
                            .fields(fieldBuilder -> fieldBuilder.field("id").field("name").field("singerName"))
                            .query(queryBuilder -> queryBuilder
                                    .match(matchBuilder -> matchBuilder.field("musicName").query(music.getMusicName())))
                    , Music.class);
        }
        List<Music> result = new LinkedList<>();
        List<Hit<Music>> hits = response.hits().hits();
        for (Hit<Music> hit : hits) {
            result.add(hit.source().setScore(hit.score()));
        }
        return result;
    }

    /**
     * 用名称查询音乐数量，是全文匹配
     *
     * @param music 音乐名称
     * @return 数量
     * @throws IOException
     * @throws ElasticsearchException
     */
    public Long selectCountByName(Music music) throws IOException, ElasticsearchException {
        CountResponse response = client.count(countRequestBuilder -> countRequestBuilder
                .index(INDEX_NAME)
                .query(queryBuilder -> queryBuilder
                        .match(matchQueryBuilder -> matchQueryBuilder.field("musicName").query(music.getMusicName()))));
        return response.count();
    }

    /**
     * 用歌词查询歌曲数量，使用全文匹配，效果和名称类似
     *
     * @param music 音乐信息
     * @return 查询结果
     * @throws IOException
     * @throws ElasticsearchException
     */
    public Long selectCountByLyric(Music music) throws IOException, ElasticsearchException {
        CountResponse response = client.count(countRequestBuilder -> countRequestBuilder
                .index(INDEX_NAME)
                .query(queryBuilder -> queryBuilder
                        .match(matchQueryBuilder -> matchQueryBuilder.field("lyric").query(music.getLyric()))));
        return response.count();
    }

    /**
     * 查询所有的音乐，只能一页页向下
     *
     * @param lastIndex 上一次查询的末尾ID
     * @return 音乐信息
     * @throws IOException
     * @throws ElasticsearchException
     */
    public List<Music> selectAllMusic(Long lastIndex) throws IOException, ElasticsearchException {
        SearchResponse<Music> response;
        if (lastIndex != null) {
            final String lastIndex1 = Long.toString(lastIndex);
            response = client.search(searchRequestBuilder -> searchRequestBuilder
                            .sort(sortBuilder -> sortBuilder.field(filedBuilder -> filedBuilder.field("id")))
                            .searchAfter(lastIndex1)
                            .index(INDEX_NAME).size(Others.PAGE_SIZE)
                            .fields(fieldBuilder -> fieldBuilder.field("id").field("name").field("singerName"))
                    , Music.class);
        } else {
            response = client.search(searchRequestBuilder -> searchRequestBuilder
                            .sort(sortBuilder -> sortBuilder.field(filedBuilder -> filedBuilder.field("id")))
                            .index(INDEX_NAME).size(Others.PAGE_SIZE)
                            .fields(fieldBuilder -> fieldBuilder.field("id").field("name").field("singerName"))
                    , Music.class);
        }
        List<Music> result = new LinkedList<>();
        List<Hit<Music>> hits = response.hits().hits();
        for (Hit<Music> hit : hits) {
            result.add(hit.source().setScore(hit.score()));
        }
        return result;
    }

    /**
     * 查询所有音乐的数量
     *
     * @return 数量
     * @throws IOException
     * @throws ElasticsearchException
     */
    public Long selectAllCount() throws IOException, ElasticsearchException {
        CountResponse response = client.count(countRequestBuilder -> countRequestBuilder
                .index(INDEX_NAME));
        return response.count();
    }

    /**
     * 用歌词查询歌曲，全文匹配，和名称类似
     *
     * @param music     歌曲信息
     * @param lastIndex 上次查询的末尾ID
     * @param lastScore 上次查询的末尾分数
     * @return 歌曲信息
     * @throws IOException
     * @throws ElasticsearchException
     */
    public List<Music> selectMusicByLyric(Music music, Long lastIndex, Double lastScore) throws IOException, ElasticsearchException {
        SearchResponse<Music> response;
        if (lastIndex != null && lastScore != null) {
            final String lastIndex1 = Long.toString(lastIndex);
            final String lastScore1 = Double.toString(lastScore);
            response = client.search(searchRequestBuilder -> searchRequestBuilder
                            .sort(sortBuilder -> sortBuilder.score(scoreBuilder -> scoreBuilder.order(SortOrder.Desc)))
                            .sort(sortBuilder -> sortBuilder.field(filedBuilder -> filedBuilder.field("id")))
                            .searchAfter(lastScore1, lastIndex1)
                            .index(INDEX_NAME).size(Others.PAGE_SIZE)
                            .fields(fieldBuilder -> fieldBuilder.field("id").field("name").field("singerName"))
                            .query(queryBuilder -> queryBuilder
                                    .match(matchBuilder -> matchBuilder.field("lyric").query(music.getLyric())))
                    , Music.class);
        } else {
            response = client.search(searchRequestBuilder -> searchRequestBuilder
                            .sort(sortBuilder -> sortBuilder.score(scoreBuilder -> scoreBuilder.order(SortOrder.Desc)))
                            .sort(sortBuilder -> sortBuilder.field(filedBuilder -> filedBuilder.field("id")))
                            .index(INDEX_NAME).size(Others.PAGE_SIZE)
                            .fields(fieldBuilder -> fieldBuilder.field("id").field("name").field("singerName"))
                            .query(queryBuilder -> queryBuilder
                                    .match(matchBuilder -> matchBuilder.field("lyric").query(music.getLyric())))
                    , Music.class);
        }
        List<Music> result = new LinkedList<>();
        List<Hit<Music>> hits = response.hits().hits();
        for (Hit<Music> hit : hits) {
            result.add(hit.source().setScore(hit.score()));
        }
        return result;
    }

    /**
     * 在用户添加到"我喜欢"后，修改歌曲的收藏数
     *
     * @param counts 歌曲id和收藏数量的map
     * @return
     * @throws IOException
     * @throws ElasticsearchException
     */
    public int updateMusicLike(Map<Object, Object> counts) throws IOException, ElasticsearchException {
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
}
