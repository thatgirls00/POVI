package org.example.povi.domain.diary.enums;


import org.example.povi.domain.user.follow.service.FollowService;

public enum Visibility {
    PUBLIC,
    FRIEND,
    PRIVATE;

    public boolean canAccess(Long viewerId, Long ownerId, FollowService followService) {
        return switch (this) {
            case PUBLIC -> true;
            case FRIEND -> followService.isMutualFollow(viewerId, ownerId);
            case PRIVATE -> false;
        };
    }
}
