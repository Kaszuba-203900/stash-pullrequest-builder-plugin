package stashpullrequestbuilder.stashpullrequestbuilder;

import hudson.model.Result;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;

import javax.xml.bind.DatatypeConverter;
import java.util.logging.Logger;

public class StashPostBuildStatus {
    private static final Logger logger = Logger.getLogger(StashPostBuildStatus.class.getName());
    private BuildStatus state;
    private String key;
    private String url;
    private String credentials;

    public StashPostBuildStatus(Result result, StashBuildTrigger trigger, String url) {
        this.state = getStatus(result);
        this.key = trigger.getProjectCode();
        this.url = url;
        this.credentials = setCredentials(trigger);
        logger.info(this.toString());
    }

    private String setCredentials(StashBuildTrigger trigger) {
        return DatatypeConverter.printBase64Binary((trigger.getUsername() + ":" + trigger.getPassword()).getBytes());
    }

    public StringEntity getFormattedEntity() {
        return new StringEntity(
                generateJsonBody(), ContentType.APPLICATION_JSON);
    }

    private String generateJsonBody() {
        return "{\"state\":\"" + this.state +
                "\",\"key\":\"" + this.key +
                "\",\"url\":\"" + this.url + "\"}";
    }

    private BuildStatus getStatus(Result result) {
        if (result == Result.NOT_BUILT)
            return BuildStatus.INPROGRESS;
        return result == Result.SUCCESS ? BuildStatus.SUCCESSFUL : BuildStatus.FAILED;
    }

    public String getCredentials() {
        return credentials;
    }

    @Override
    public String toString() {
        return "StashPostBuildStatus{" +
                "state=" + state +
                ", key='" + key + '\'' +
                ", url='" + url + '\'' +
                ", credentials='" + credentials + '\'' +
                '}';
    }
}

enum BuildStatus {
    SUCCESSFUL, FAILED, INPROGRESS
}
