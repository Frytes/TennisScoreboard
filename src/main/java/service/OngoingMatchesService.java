package service;

import model.Match;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class OngoingMatchesService {
    private static final OngoingMatchesService INSTANCE = new OngoingMatchesService();

    private final Map<UUID, Match> matches = new ConcurrentHashMap<>();

    private OngoingMatchesService() {
    }

    public static OngoingMatchesService getInstance() {
        return INSTANCE;
    }

    public void add(Match match) {
        matches.put(match.getUuid(), match);
    }

    public Match get(UUID uuid) {
        return matches.get(uuid);
    }

    public void remove(UUID uuid) {
        matches.remove(uuid);
    }
}