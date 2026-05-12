package com.gym.Elite.Gym.attendanceEvent.validator;

import com.gym.Elite.Gym.attendanceEvent.enums.AttendanceActorType;
import com.gym.Elite.Gym.attendanceEvent.exception.AttendanceException;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class ValidationStrategyFactory {

    private final List<AttendanceValidationStrategy> strategies;
    private Map<AttendanceActorType, AttendanceValidationStrategy> strategyMap;

    @PostConstruct
    public void init() {
        strategyMap = strategies.stream()
                .collect(Collectors.toMap(
                        AttendanceValidationStrategy::getSupportedType,
                        Function.identity()
                ));
    }

    public AttendanceValidationStrategy getStrategy(AttendanceActorType type) {
        AttendanceValidationStrategy strategy = strategyMap.get(type);
        if (strategy == null) {
            throw new AttendanceException("No validation strategy found for actor type: " + type);
        }
        return strategy;
    }
}
