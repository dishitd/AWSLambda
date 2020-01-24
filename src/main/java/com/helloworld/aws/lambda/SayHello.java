package com.helloworld.aws.lambda;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.events.SQSEvent;
import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.AmazonSQSClientBuilder;
import com.amazonaws.services.sqs.model.MessageAttributeValue;
import com.amazonaws.services.sqs.model.SendMessageRequest;
import org.apache.commons.lang3.StringUtils;

public class SayHello implements RequestHandler<SQSEvent, Object> {
    public Object handleRequest(SQSEvent event, Context context){

        String queueUrl = "https://sqs.ap-southeast-2.amazonaws.com/507725842651/helloWorldResponseQueue";
        AmazonSQS sqs = AmazonSQSClientBuilder.standard()
//                .withCredentials(new AWSStaticCredentialsProvider(credentials))
                .withRegion(Regions.AP_SOUTHEAST_2)
                .build();
        Map<String, MessageAttributeValue> messageAttributes = new HashMap<>();
        messageAttributes.put("AttributeOne", new MessageAttributeValue()
                .withStringValue("Attribute 1")
                .withDataType("String"));
        String inputMessage = StringUtils.join(String.valueOf(event.getRecords()
                .stream()
                .map(SQSEvent.SQSMessage::getBody)
                .collect(Collectors.toList())), ",");
        SendMessageRequest sendMessageStandardQueue = new SendMessageRequest()
                .withQueueUrl(queueUrl)
                .withMessageBody(inputMessage)
                .withDelaySeconds(30)
                .withMessageAttributes(messageAttributes);
        sqs.sendMessage(sendMessageStandardQueue);
        return null;
    }
}