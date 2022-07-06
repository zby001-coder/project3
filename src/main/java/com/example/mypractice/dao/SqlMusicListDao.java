package com.example.mypractice.dao;

import com.example.mypractice.model.database.MusicList;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@Mapper
public interface SqlMusicListDao {
    int addMusicList(@Param("musicList") MusicList musiclist);

    int deleteMusicList(@Param("musicList") MusicList musicList);

    int updateMusicList(@Param("musicList") MusicList musicList);

    MusicList selectMusicListById(@Param("musicList") MusicList musicList);

    List<MusicList> selectMusicListByAuthorId(@Param("musicList") MusicList musicList, @Param("lastIndex") Long lastIndex);

    int selectMusicListCountByAuthorId(@Param("musicList") MusicList musicList);

    List<MusicList> selectAllMusicList(@Param("lastIndex") Long lastIndex);

    int selectAllMusicListCount();

    int banMusicList(@Param("musicList") MusicList musicList);

    int unsealMusicList(@Param("musicList") MusicList musicList);

    int addListToLike(@Param("listId") List<Long> listId, @Param("userId") Long userId);

    int deleteListFromLike(@Param("listId") List<Long> listId, @Param("userId") Long userId);

    List<MusicList> selectListFromLike(@Param("lastIndex") Long lastIndex, @Param("userId") Long userId);

    int selectListCountFromLike(@Param("userId") Long userId);

    /**
     * 当前的歌单是否放在"我喜欢"中
     *
     * @param listId
     * @param userId
     * @return
     */
    int ifLiked(@Param("listId") Long listId, @Param("userId") Long userId);

    /**
     * 当前的歌单是否为改用户创建
     *
     * @param listId
     * @param userId
     * @return
     */
    int ifCreated(@Param("listId") Long listId, @Param("userId") Long userId);
}
