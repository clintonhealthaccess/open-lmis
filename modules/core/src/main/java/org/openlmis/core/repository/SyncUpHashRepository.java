package org.openlmis.core.repository;

import lombok.NoArgsConstructor;
import org.openlmis.core.repository.mapper.SyncUpHashMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@NoArgsConstructor
public class SyncUpHashRepository {

    private SyncUpHashMapper syncUpHashMapper;

    @Autowired
    public SyncUpHashRepository(SyncUpHashMapper syncUpHashMapper) {
        this.syncUpHashMapper = syncUpHashMapper;
    }

    public void save(String hash) {
        syncUpHashMapper.insert(hash);
    }

    public boolean hashExists(String hash) {
        return syncUpHashMapper.find(hash).size() > 0;
    }

    public void deleteSyncUpHashes(List<String> syncUpHashes) {
        String hashes = syncUpHashes.toString().replace("[", "{").replace("]", "}");
        syncUpHashMapper.deleteSyncUpHashes(hashes);
    }
}
