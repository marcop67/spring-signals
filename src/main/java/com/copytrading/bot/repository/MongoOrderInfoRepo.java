package com.copytrading.bot.repository;

import com.copytrading.bot.model.MongoOrderInfo;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface MongoOrderInfoRepo extends MongoRepository<MongoOrderInfo, String> {

}
