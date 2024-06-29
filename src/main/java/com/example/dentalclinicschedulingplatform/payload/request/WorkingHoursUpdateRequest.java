package com.example.dentalclinicschedulingplatform.payload.request;

import com.example.dentalclinicschedulingplatform.entity.DayInWeek;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalTime;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class WorkingHoursUpdateRequest {
    @NotNull(message = "Day can not be null")
    private DayInWeek day;

    @NotNull(message = "Start time can not be null")
    private LocalTime startTime;

    @NotNull(message = "End time can not be null")
    private LocalTime endTime;

    @NotNull(message = "Clinic branch id can not be null")
    private Long clinicId;
}
