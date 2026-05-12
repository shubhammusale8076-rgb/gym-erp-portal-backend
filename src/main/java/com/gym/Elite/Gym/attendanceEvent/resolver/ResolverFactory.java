package com.gym.Elite.Gym.attendanceEvent.resolver;

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
public class ResolverFactory {

    private final List<AttendanceActorResolver> resolvers;
    private Map<AttendanceActorType, AttendanceActorResolver> resolverMap;

    @PostConstruct
    public void init() {
        resolverMap = resolvers.stream()
                .collect(Collectors.toMap(
                        AttendanceActorResolver::getSupportedType,
                        Function.identity()
                ));
    }

    public AttendanceActorResolver getResolver(AttendanceActorType type) {
        AttendanceActorResolver resolver = resolverMap.get(type);
        if (resolver == null) {
            throw new AttendanceException("No resolver found for actor type: " + type);
        }
        return resolver;
    }
}
