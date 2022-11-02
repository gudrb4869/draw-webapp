package hongik.ce.jolup.module.match.endpoint.form;

import hongik.ce.jolup.module.match.domain.entity.Match;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class LocationForm {

    private double longitude = 127.1052133;
    private double latitude = 37.3595316;
    private String jibunAddress = "정자동 178-1";
    private String roadAddress;

    public static LocationForm from(Match match) {
        LocationForm locationForm = new LocationForm();
        if (match.getLongitude() != null) {
            locationForm.longitude = match.getLongitude();
        }
        if (match.getLatitude() != null) {
            locationForm.latitude = match.getLatitude();
        }
        if (match.getJibunAddress() != null) {
            locationForm.jibunAddress = match.getJibunAddress();
        }
        locationForm.roadAddress = match.getRoadAddress();
        return locationForm;
    }
}
