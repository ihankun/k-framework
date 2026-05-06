package io.hankun.framework.ai.store.mongo;

import com.alibaba.fastjson2.JSON;
import io.hankun.framework.ai.common.record.IDetailRecord;
import io.hankun.framework.ai.store.history.detail.ChatModelRecord;
import io.hankun.framework.ai.store.history.detail.EmbeddingRecord;
import io.hankun.framework.ai.store.history.detail.RerankRecord;
import io.hankun.framework.ai.store.history.detail.TaskToolCallRecord;
import io.hankun.framework.ai.store.memory.shortTerm.MessageConvertor;
import org.bson.Document;
import org.jetbrains.annotations.NotNull;
import org.springframework.ai.chat.messages.Message;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.ReadingConverter;
import org.springframework.data.convert.WritingConverter;
import org.springframework.data.mongodb.core.convert.MongoCustomConversions;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @description:
 * @className: MsunMongoDbConversions
 * @createAt: 2025/10/15 13:57
 * @author: hankun
 */
@Component
public class MsunMongoDbConversions extends MongoCustomConversions {
    public MsunMongoDbConversions() {
        super(List.of(new MessageReadConverter(), new MessageWriteConverter()));
    }


    @ReadingConverter
    public static class MessageReadConverter implements Converter<Document, Message> {

        @Override
        public Message convert(Document source) {
            return MessageConvertor.convert(source.toJson());
        }

    }

    @WritingConverter
    public static class MessageWriteConverter implements Converter<Message, Document> {

        @Override
        public Document convert(@NotNull Message source) {
            return Document.parse(MessageConvertor.convert(source));
        }

    }

    @ReadingConverter
    public static class DetailRecordConverter implements Converter<Document, IDetailRecord> {

        @Override
        public IDetailRecord convert(Document source) {
            String key = source.getString("type");
            return switch (key) {
                case ChatModelRecord.KEY -> JSON.parseObject(source.toJson(), ChatModelRecord.class);
                case EmbeddingRecord.KEY -> JSON.parseObject(source.toJson(), EmbeddingRecord.class);
                case RerankRecord.KEY -> JSON.parseObject(source.toJson(), RerankRecord.class);
                case TaskToolCallRecord.KEY -> JSON.parseObject(source.toJson(), TaskToolCallRecord.class);
                case null, default -> null;
            };
        }

    }
}
