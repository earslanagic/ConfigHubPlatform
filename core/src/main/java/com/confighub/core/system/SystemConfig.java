package com.confighub.core.system;

import com.confighub.core.store.APersisted;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;

/**
 * Entity for persisting configuration data for ConfigHub instances.
 */
@Entity
@Cacheable
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
@Getter
@Setter
@Table(
        uniqueConstraints = @UniqueConstraint(columnNames = {"config_group", "config_key"}),
        indexes = {@Index(name = "SYSTEM_CONFIG_INDEX", columnList = "id, config_group, config_key")}
)
@NamedQueries({
      @NamedQuery(name = "SysConfig.byGroup",
                  query = "SELECT c FROM SystemConfig c WHERE configGroup=:groupName"),
      @NamedQuery(name = "SysConfig.byKey",
                  query = "SELECT c FROM SystemConfig c WHERE configGroup=:groupName AND key=:key")
})
public class SystemConfig
        extends APersisted
{
    public enum ConfigGroup
    {
        LDAP
    }

    @Id
    @GeneratedValue
    @Setter(AccessLevel.NONE)
    private Long id;

    @Column(name = "config_key", nullable = false, unique = true)
    private String key;

    @Column(name = "value",
            length = 8192)
    private String value;

    @Enumerated(EnumType.STRING)
    @Column(name = "config_group", nullable = false)
    private ConfigGroup configGroup;

    @Column(name = "encrypted")
    private boolean encrypted = false;

    public JsonObject toJson()
    {
        JsonObject json = new JsonObject();
        json.addProperty("key", this.key);

        if (!this.encrypted)
        {
            Gson gson = new Gson();
            json.add("value", gson.fromJson(this.value, JsonObject.class));
        }

        json.addProperty("configGroup", this.configGroup.name());
        json.addProperty("encrypted", this.encrypted);

        return json;
    }

    @Override
    public ClassName getClassName()
    {
        return ClassName.Configuration;
    }
}