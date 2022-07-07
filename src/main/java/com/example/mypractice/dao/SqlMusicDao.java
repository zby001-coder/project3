package com.example.mypractice.dao;

import com.example.mypractice.model.database.Music;
import com.example.mypractice.model.database.MusicList;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@Mapper
public interface SqlMusicDao {
    int addMusic(@Param("music") Music music);

    int deleteMusic(@Param("music") Music music);

    int updateMusic(@Param("music") Music music);

    int addMusicToList(@Param("musicList") MusicList musicList);

    int deleteMusicFromList(@Param("musicList") MusicList musicList);

    int addMusicToLike(@Param("musicId") List<Long> musicId, @Param("userId") Long userId);

    int deleteMusicFromLike(@Param("musicId") List<Long> musicId, @Param("userId") Long userId);

    List<Music> selectMusicFromLike(@Param("lastIndex") Long lastIndex, @Param("userId") Long userId);

    int selectMusicCountFromLike(@Param("userId") Long userId);

    /**
     * 当前的歌曲是否放在"我喜欢"中
     *
     * @param musicId
     * @param userId
     * @return
     */
    int ifLiked(@Param("musicId") Long musicId, @Param("userId") Long userId);

    /**
     * 当前歌曲是否放在歌单中
     *
     * @param listId
     * @param musicId
     * @return
     */
    int ifCollected(@Param("listId") Long listId, @Param("musicId") Long musicId);
}
