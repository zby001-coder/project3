package com.example.mypractice;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.core.*;
import co.elastic.clients.elasticsearch.core.search.Hit;
import co.elastic.clients.json.jackson.JacksonJsonpMapper;
import co.elastic.clients.transport.ElasticsearchTransport;
import co.elastic.clients.transport.rest_client.RestClientTransport;
import com.example.mypractice.commons.util.SnowflakeIdGenerator;
import com.example.mypractice.dao.EsMusicDao;
import com.example.mypractice.dao.EsMusicListDao;
import com.example.mypractice.dao.SqlMusicListDao;
import com.example.mypractice.dao.UserDao;
import com.example.mypractice.model.database.Music;
import com.example.mypractice.model.database.MusicList;
import com.example.mypractice.model.database.User;
import com.example.mypractice.service.UserService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.*;

@SpringBootTest
class MyPracticeApplicationTests {
    @Autowired
    StringRedisTemplate stringRedisTemplate;
    @Autowired
    RedisTemplate<String, Object> redisTemplate;
    @Autowired
    UserDao userDao;
    @Autowired
    UserService userService;
    @Autowired
    SnowflakeIdGenerator snowflakeIdGenerator;
    @Autowired
    ObjectMapper objectMapper;
    @Autowired
    EsMusicDao esMusicDao;
    @Autowired
    SqlMusicListDao sqlMusicListDao;
    @Autowired
    EsMusicListDao esMusicListDao;
    // Create the low-level client
    RestClient restClient = RestClient.builder(
            new HttpHost("localhost", 9200)).build();
    // Create the transport with a Jackson mapper
    ElasticsearchTransport transport = new RestClientTransport(
            restClient, new JacksonJsonpMapper());
    // And create the API client
    ElasticsearchClient client = new ElasticsearchClient(transport);

    @Test
    void contextLoads() throws Exception {
        System.out.println(objectMapper.writeValueAsString(new Music()));
    }

    @Test
    void test1() {
        for (int i = 0; i < 1000; i++) {
            long id = snowflakeIdGenerator.nextId();
            Long tb = (id >> 22) & 3L;
            System.out.println(tb);
        }
    }

    @Test
    void test2() throws IOException {
        FileInputStream fileInputStream = new FileInputStream("C:\\Users\\31405\\Desktop\\lyric.txt");
        byte[] buffer = new byte[1024 * 1024 * 2];
        int len = fileInputStream.read(buffer);
        String lyric = new String(buffer, 0, len);
        //批处理，用bulkBuilder创建一个bulkRequest
        //用bulkOperationBuilder创建一个bulkOperation，放入bulkRequest中
        //用indexOperationBuilder创建多个IndexOperation，放入bulkOperation中，如果用其他类型的OperationBuilder还可以做其他操作
        //最后用client发送bulkRequest出去
        for (int i = 0; i < 10; i++) {
            BulkRequest.Builder bulkBuilder = new BulkRequest.Builder();
            for (int j = 0; j < 10000; j++) {
                Music music = new Music(snowflakeIdGenerator.nextId(), "修炼爱情", "林俊杰").setMusicUrl("1").setLyric(lyric).setMvUrl("1").setDescription("1").setLikeCount(1).setReleaseTime(new Date(1L));
                bulkBuilder.operations(bulkOperationBuilder -> bulkOperationBuilder
                        .index(indexOperationBuilder -> indexOperationBuilder
                                .index("music")
                                .id(Long.toString(music.getId()))
                                .document(music)));
            }
            BulkResponse bulk = client.bulk(bulkBuilder.build());
            System.out.println("size: " + bulk.items().size() + "  " + "time: " + bulk.took());
        }
    }

    @Test
    void test3() throws IOException {
        GetResponse<User> response = client.get(g -> g
                        .index("user")
                        .id("1"),
                User.class
        );

        System.out.println(response.seqNo());
        System.out.println(response.primaryTerm());
    }

    @Test
    void test4() throws IOException {
        //search需要一个SearchRequest来作为参数，生成SearchRequest的过程被包装了三层
        //最外层是SearchRequestBuilder，它用来确定查询的索引是哪个，分页怎么分，还有一些和具体查询无关的东西，它生成一个SearchRequest，同时需要一个Query
        //第二层是QueryBuilder，它用来确定查询的方式是什么，比如Match搜索，Bool搜索等等，它生成一个抽象Query，同时需要一个具体的Query
        //第三层确定要查询的字段是什么，查询时的条件是什么，同时不同的查询方式使用不同的Builder，比如这里的Match方式就是用MatchBuilder，它生成一个具体的Query
//        String searchText = "谁说";
//        SearchResponse<User> response = client.search(searchRequestBuilder -> searchRequestBuilder.scroll(timeBuilder -> timeBuilder.time("10m"))
//                        .index("user")
//                        .size(30)
//                        .query(queryBuilder -> queryBuilder
//                                .match(MatchBuilder -> MatchBuilder
//                                        .field("userName")
//                                        .query(searchText)
//                                )
//                        ),
//                User.class
//        );
        ScrollResponse<User> result = client.scroll(scrollRequestBuilder -> scrollRequestBuilder.scrollId("FGluY2x1ZGVfY29udGV4dF91dWlkDXF1ZXJ5QW5kRmV0Y2gBFi1kNldIM3pRU215LTRwYVBzYkR6Q1EAAAAAAAAATBZGNHJkaXlYRFNFMlpDWUg4UVl6LWZR"), User.class);
        System.out.println(result.scrollId());
        for (Hit<User> hit : result.hits().hits()) {
            System.out.println(hit.source().getId());
        }
//        TotalHits total = response.hits().total();
//        boolean isExactResult = total.relation() == TotalHitsRelation.Eq;
//
//        if (isExactResult) {
//            System.out.println("There are " + total.value() + " results");
//        } else {
//            System.out.println("There are more than " + total.value() + " results");
//        }
//        for (Hit<User> hit : response.hits().hits()) {
//            System.out.println(hit.source().getId());
//        }
    }

    @Test
    void test5() throws IOException {
        FileInputStream fileInputStream = new FileInputStream("C:\\Users\\31405\\Desktop\\lyric.txt");
        byte[] buffer = new byte[1024 * 1024 * 2];
        int len = fileInputStream.read(buffer);
        String lyric = new String(buffer, 0, len);
        Music music = new Music(snowflakeIdGenerator.nextId(), "修炼爱情", "林俊杰").setMusicUrl("1").setLyric(lyric).setMvUrl("1").setDescription("1").setLikeCount(1).setReleaseTime(new Date(1L));
        esMusicDao.addMusic(music);
    }

    @Test
    void test6() throws IOException {
        esMusicDao.deleteMusic(new Music(2478097668505600L));
    }

    @Test
    void test7() throws IOException {
        System.out.println(esMusicDao.updateMusic(new Music(2478097668505600L, "张三", "李四")));
    }

    @Test
    void test8() throws IOException {
        System.out.println(esMusicDao.selectMusicById(new Music(2481071983689728L)));
    }

    @Test
    void test9() throws Exception {
        Long lastIndex = null;
        Double lastScore = null;
        HashSet<Long> ids = new HashSet<>();
        for (int i = 0; ; i++) {
            List<Music> result = esMusicDao.selectMusicByLyric(new Music().setLyric("谁说"), lastIndex, lastScore);
            int origin = ids.size();
            for (Music music : result) {
                ids.add(music.getId());
            }
            if (ids.size() != origin + result.size()) {
                System.out.println(origin);
                System.out.println(origin + result.size());
                System.out.println(ids.size());
                throw new Exception("WRONG!");
            }
            Music music = result.get(result.size() - 1);
            lastIndex = music.getId();
            lastScore = music.getScore();
            System.out.println(i);
        }
    }

    @Test
    void test10() throws Exception {
        Long lastIndex = null;
        Double lastScore = null;
        HashSet<Long> ids = new HashSet<>();
        for (int i = 0; ; i++) {
            List<Music> result = esMusicDao.selectMusicByName(new Music().setMusicName("爱情"), lastIndex, lastScore);
            int origin = ids.size();
            for (Music music : result) {
                ids.add(music.getId());
            }
            if (ids.size() != origin + result.size()) {
                System.out.println(origin);
                System.out.println(origin + result.size());
                System.out.println(ids.size());
                throw new Exception("WRONG!");
            }
            Music music = result.get(result.size() - 1);
            lastIndex = music.getId();
            lastScore = music.getScore();
            System.out.println(i);
        }
    }

    @Test
    void test11() throws IOException {
        System.out.println(esMusicDao.selectCountByName(new Music().setMusicName("爱情")));
    }

    @Test
    void test12() throws IOException {
        String a = "想你的夜 - 关喆\\n" +
                "词：关喆\\n" +
                "曲：关喆\\n" +
                "你知道吗\\n" +
                "没有你的日子我有多想你\\n" +
                "分手那天\\n" +
                "我看着你走远\\n" +
                "所有承诺化成了句点\\n" +
                "独自守在空荡的房间\\n" +
                "爱与痛在我心里纠缠\\n" +
                "我们的爱走到了今天\\n" +
                "是不是我太自私了一点\\n" +
                "如果爱可以重来\\n" +
                "我会为你放弃一切\\n" +
                "想你的夜\\n" +
                "多希望你能在我身边\\n" +
                "不知道你心里还能否为我改变\\n" +
                "哦 想你的夜\\n" +
                "求你让我再爱你一遍\\n" +
                "让爱再回到原点\\n" +
                "分手那天\\n" +
                "我看着你走远\\n" +
                "所有承诺化成了句点\\n" +
                "独自守在空荡的房间\\n" +
                "爱与痛在我心里纠缠\\n" +
                "我们的爱走到了今天\\n" +
                "是不是我太自私了一点\\n" +
                "如果爱可以重来\\n" +
                "我会为你放弃一切\\n" +
                "想你的夜\\n" +
                "多希望你能在我身边\\n" +
                "不知道你心里还能否为我改变\\n" +
                "哦 想你的夜\\n" +
                "求你让我再爱你一遍\\n" +
                "让爱再回到原点\\n" +
                "想你的夜\\n" +
                "多希望你能在我身边\\n" +
                "不知道你心里还能否为我改变\\n" +
                "哦 想你的夜\\n" +
                "求你让我再爱你一遍\\n" +
                "让爱再回到原点\\n" +
                "回来吧 我等你\\n";
        FileOutputStream fileOutputStream = new FileOutputStream("C:\\Users\\31405\\Desktop\\lyric1.txt");
        fileOutputStream.write(a.getBytes());
    }

    @Test
    void test13() throws Exception {
        Long lastIndex = null;
        HashSet<Long> ids = new HashSet<>();
        for (int i = 0; ; i++) {
            List<Music> musicList = esMusicDao.selectAllMusic(lastIndex);
            int origin = ids.size();
            for (Music music : musicList) {
                ids.add(music.getId());
            }
            if (origin + musicList.size() != ids.size()) {
                throw new Exception("WRONG!");
            }
            lastIndex = musicList.get(musicList.size() - 1).getId();
            System.out.println(i);
        }
    }

    @Test
    void test14() throws IOException {
        System.out.println(esMusicDao.selectAllCount());
    }

    @Test
    void test15() throws IOException {
        byte[] buffer = new byte[1024 * 1024 * 20];
        Long start = System.currentTimeMillis();
        FileOutputStream fileOutputStream = new FileOutputStream("C:\\Users\\31405\\Desktop\\m.txt");
        fileOutputStream.write(buffer);
        Long end = System.currentTimeMillis();
        System.out.println(end - start);
    }

    @Test
    void test16() throws IOException {
        byte[] content = new byte[1024 * 1024 * 1024];
        Long start = System.currentTimeMillis();
        ByteBuffer buffer = ByteBuffer.allocate(content.length);
        buffer.put(content);
        buffer.flip();
        FileOutputStream fileOutputStream = new FileOutputStream("C:\\Users\\31405\\Desktop\\m.txt");
        FileChannel channel = fileOutputStream.getChannel();
        channel.write(buffer);
        Long end = System.currentTimeMillis();
        System.out.println(end - start);
    }

    @Test
    void test17() throws IOException {
        List<Music> musicList = new LinkedList<>();
        musicList.add(new Music(2481071983689728L));
        musicList.add(new Music(2481072017244161L));
        System.out.println(esMusicDao.selectMusicByIdBulk(musicList));
//        System.out.println(musicDao.selectMusicById(new Music(2481072017244161L)));
    }

    @Test
    void test18() throws JsonProcessingException {
        List<Music> music = new LinkedList<>();
        music.add(new Music(12L));
        music.add(new Music(13L));
        MusicList musicList = objectMapper.readValue("{\"id\":\"1\",\"name\":null,\"musicCount\":0,\"author\":null,\"isBanned\":null,\"description\":null,\"createTime\":null,\"likeCount\":null,\"music\":[{\"id\":\"12\",\"musicName\":null,\"singerName\":null,\"description\":null,\"releaseTime\":null,\"musicUrl\":null,\"mvUrl\":null,\"coverUrl\":null,\"likeCount\":null,\"lyric\":null,\"score\":null},{\"id\":\"13\",\"musicName\":null,\"singerName\":null,\"description\":null,\"releaseTime\":null,\"musicUrl\":null,\"mvUrl\":null,\"coverUrl\":null,\"likeCount\":null,\"lyric\":null,\"score\":null}],\"coverUrl\":null,\"score\":null}", MusicList.class);
        System.out.println(musicList);
    }

    @Test
    void test19() throws IOException {
        List<Music> music = new LinkedList<>();
        music.add(new Music(12L));
        music.add(new Music(13L));
        esMusicListDao.deleteMusicList(new MusicList(1L));
        MusicList musicList = new MusicList(1L,"123").setMusicCount(0);
        esMusicListDao.addMusicList(musicList);
    }

    @Test
    void test20() throws IOException {
        List<Music> music = new LinkedList<>();
        music.add(new Music(12L));
        music.add(new Music(13L));
    }
}
