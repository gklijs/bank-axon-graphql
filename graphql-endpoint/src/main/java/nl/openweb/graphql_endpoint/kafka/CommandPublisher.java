package nl.openweb.graphql_endpoint.kafka;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import nl.openweb.api.bank.command.CreateBankAccountCommand;
import nl.openweb.data.ConfirmAccountCreation;
import nl.openweb.graphql_endpoint.mapper.UuidMapper;
import nl.openweb.graphql_endpoint.properties.KafkaProperties;
import org.apache.avro.specific.SpecificRecordBase;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@AllArgsConstructor
@Service
public class CommandPublisher {

    private final KafkaProperties kafkaProperties;
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final CommandGateway commandGateway;

    public void publish(String username, SpecificRecordBase avroRecord) {
        kafkaTemplate.send(kafkaProperties.getCommandsTopic(), username, avroRecord);
        if(avroRecord instanceof ConfirmAccountCreation){
            log.info("Going to send command");
            ConfirmAccountCreation command = (ConfirmAccountCreation) avroRecord;
            CreateBankAccountCommand axonCommand = new CreateBankAccountCommand(UuidMapper.fromAvroUuid(command.getId()).toString(), command.getUsername());
            commandGateway.send(axonCommand, (o,n) -> {
                log.info("First thing {}", o);
                log.info("Second thing {}", n);
            });
        }
    }
}
