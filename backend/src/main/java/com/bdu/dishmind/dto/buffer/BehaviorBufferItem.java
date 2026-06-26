package com.bdu.dishmind.dto.buffer;

import lombok.Data;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class BehaviorBufferItem implements Serializable {
    private Long userId;
    private Long recipeId;
    private String behaviorType;
    private Integer durationSeconds;
    /** 该菜谱关联的全部 tagId，避免消费者再查 recipe_tag */
    private List<Integer> tagIds;
    /** 预计算好的权重增量 */
    private Double weightDelta;
    /** 入队时间，刷盘时作为行为发生时间 */
    private LocalDateTime timestamp;
}
