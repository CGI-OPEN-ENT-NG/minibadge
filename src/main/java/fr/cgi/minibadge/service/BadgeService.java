package fr.cgi.minibadge.service;

import fr.cgi.minibadge.model.Badge;
import io.vertx.core.Future;

import java.util.List;

public interface BadgeService {
    /**
     * Creates badges if not exists
     *
     * @param typeId   type identifier
     * @param ownerIds badge owner identifiers
     * @return return future
     */
    Future<Void> createBadges(long typeId, List<String> ownerIds);

    /**
     * Creates badges if not exists
     *
     * @param ownerId owner identifier
     * @param query to filter on badge type label
     * @return return badge list of current user
     */
    Future<List<Badge>> getBadges(String ownerId, String query);

    /**
     * Privatize badge from type and current user
     *
     * @param ownerId owner identifier
     * @param typeId  Type identifier
     * @return return future ending process
     */
    Future<Void> privatizeBadge(String ownerId, long typeId);

    /**
     * Refuse badge from type and current user
     *
     * @param ownerId owner identifier
     * @param typeId  Type identifier
     * @return return future ending process
     */
    Future<Void> refuseBadge(String ownerId, long typeId);


}
