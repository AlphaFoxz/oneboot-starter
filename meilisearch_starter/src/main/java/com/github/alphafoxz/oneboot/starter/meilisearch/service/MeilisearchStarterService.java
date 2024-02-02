package com.github.alphafoxz.oneboot.starter.meilisearch.service;

import com.github.alphafoxz.oneboot.core.toolkit.coding.ArrayUtil;
import com.github.alphafoxz.oneboot.core.toolkit.coding.JSONUtil;
import com.meilisearch.sdk.Client;
import com.meilisearch.sdk.SearchRequest;
import com.meilisearch.sdk.exceptions.MeilisearchException;
import com.meilisearch.sdk.model.Key;
import com.meilisearch.sdk.model.SearchResult;
import com.meilisearch.sdk.model.TaskStatus;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Slf4j
@Service
public class MeilisearchStarterService {
    //    @Resource
//    private MeilisearchStarterProperties meilisearchStarterProperties;
    @Resource
    private Client client;

    public void createApiKey() {
        long t = System.currentTimeMillis();
        Date expDate = new Date(t + 1000 * 60 * 60 * 24 * 365);
        Key keyInfo = new Key();
        keyInfo.setDescription("Add documents: Products API key");
        keyInfo.setActions(new String[]{"*"});
        keyInfo.setIndexes(new String[]{"*"});
        keyInfo.setExpiresAt(expDate);
        try {
            getClient().createKey(keyInfo);
        } catch (MeilisearchException e) {
            log.error("createApiKey error: ", e);
        }
    }

    @Nullable
    public String getApiKey() {
        Key[] keys = null;
        try {
            keys = getClient().getKeys().getResults();
        } catch (MeilisearchException e) {
            throw new RuntimeException(e);
        }
        if (ArrayUtil.isEmpty(keys)) {
            return null;
        }
        return keys[0].getKey();
    }

    public boolean addDocuments(@NonNull String index, @NonNull List<?> documents) {
        return addDocuments(index, JSONUtil.toJsonStr(documents));
    }

    public boolean addDocuments(@NonNull String index, @NonNull String documentsJsonArr) {
        try {
            TaskStatus status = getClient().index(index).addDocuments(documentsJsonArr).getStatus();
            return TaskStatus.SUCCEEDED.equals(status);
        } catch (MeilisearchException e) {
            log.error("addDocuments error: ", e);
        }
        return false;
    }

    public boolean deleteIndex(@NonNull String index) {
        try {
            TaskStatus status = getClient().deleteIndex(index).getStatus();
            return TaskStatus.SUCCEEDED.equals(status);
        } catch (MeilisearchException e) {
            log.error("deleteIndex error: ", e);
        }
        return false;
    }

    public boolean deleteDocuments(@NonNull String index, @NonNull List<String> documentsIds) {
        try {
            TaskStatus status = getClient().index(index).deleteDocuments(documentsIds).getStatus();
            return TaskStatus.SUCCEEDED.equals(status);
        } catch (MeilisearchException e) {
            log.error("deleteDocuments error: ", e);
        }
        return false;
    }

    public <T extends Serializable> T getDocument(@NonNull String index, @NonNull String id, @NonNull Class<T> targetClass) {
        try {
            return getClient().index(index).getDocument(id, targetClass);
        } catch (MeilisearchException e) {
            log.error("getDocument error: ", e);
        }
        return null;
    }

    @Nullable
    public SearchResult search(@NonNull String index, @NonNull String str) {
        try {
            //TODO 测试str是否支持空，如果是空就应该返回无条件查询列表？
            return getClient().index(index).search(str);
        } catch (MeilisearchException e) {
            log.error("search error: ", e);
        }
        return null;
    }

    public SearchResult search(@NonNull String index, @NonNull SearchRequest searchRequest) {
        try {
            return (SearchResult) getClient().index(index).search(searchRequest);
        } catch (MeilisearchException e) {
            log.error("search error: ", e);
        }
        return null;
    }

    private Client getClient() {
        return client;
//        return new Client(new Config(meilisearchStarterProperties.getUrl(), meilisearchStarterProperties.getMasterKey()));
    }
}
