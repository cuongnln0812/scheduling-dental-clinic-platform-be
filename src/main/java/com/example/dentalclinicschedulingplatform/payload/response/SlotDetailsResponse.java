package com.example.dentalclinicschedulingplatform.payload.response;

import com.example.dentalclinicschedulingplatform.entity.DayInWeek;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SlotDetailsResponse {
    public Long slotId;
    public int slotNo;
    public LocalTime startTime;
    public LocalTime endTime;
    public boolean status;
}
