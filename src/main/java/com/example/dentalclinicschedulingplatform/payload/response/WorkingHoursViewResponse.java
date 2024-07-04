package com.example.dentalclinicschedulingplatform.payload.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class WorkingHoursViewResponse {
    public String day;
    private LocalDateTime createdDate;
    private LocalDateTime modifiedDate;
    public List<SlotDetailsResponse> slots;
}
