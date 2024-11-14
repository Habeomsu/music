package main.music.model;

import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Data
@NoArgsConstructor
public class Tracks {
    private String trackId;
    private String track_name;
    private String artist;
    private String album;
    private String albumImage_url;
    private String preview_url;

}
