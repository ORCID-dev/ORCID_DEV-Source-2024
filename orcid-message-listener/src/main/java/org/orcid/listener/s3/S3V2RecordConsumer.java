package org.orcid.listener.s3;

import java.util.function.Consumer;

import javax.annotation.Resource;

import org.orcid.utils.listener.LastModifiedMessage;
import org.orcid.utils.listener.RetryMessage;
import org.springframework.stereotype.Component;

@Component
public class S3V2RecordConsumer implements Consumer<LastModifiedMessage> {

    @Resource
    S3MessageProcessorAPIV2 proc;

    @Override
    public void accept(LastModifiedMessage message) {
        proc.update(message.getOrcid());
    }

    public void accept(RetryMessage message) {
        proc.update(message.getOrcid());
    }
}
