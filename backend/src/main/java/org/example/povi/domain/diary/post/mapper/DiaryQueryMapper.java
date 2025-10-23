package org.example.povi.domain.diary.post.mapper;


import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * JPQL의 집계 결과(Object[])를
 * (postId → count) 형태의 Map으로 변환하는 유틸리티 클래스.
 */
public final class DiaryQueryMapper {

    private DiaryQueryMapper() {
    }

    public static Map<Long, Long> toCountMap(List<Object[]> rows) {
        Map<Long, Long> m = new HashMap<>();
        if (rows == null) return m;
        for (Object[] r : rows) {
            m.put((Long) r[0], (Long) r[1]);
        }
        return m;
    }
}