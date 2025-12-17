package com.ddaaniel.queue.domain.model;

import java.sql.Timestamp;

import org.hibernate.annotations.Check;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.Type;
import org.hibernate.type.SqlTypes;
import org.springframework.data.annotation.TypeAlias;

import com.ddaaniel.queue.domain.model.dto.SettingsData;
import com.ddaaniel.queue.service.JsonAttributeConverter;

import io.hypersistence.utils.hibernate.type.json.JsonBinaryType;
import io.hypersistence.utils.hibernate.type.json.JsonType;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity(name = "tb_tenant")
public class Tenant {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "tenant_id")
  private Long tenantId;

  @Column(name = "tenant_name", nullable = false)
  private String tenantName;

  @Column(name = "domain", unique = true, nullable = false)
  private String domain;

  @Column(name = "subscription_plan", length = 50, nullable = false)
  private String subscriptionPlan = "free";

  @Column(name = "status", length = 20, nullable = false)
  @Check(constraints = "status IN ('active', 'suspended', 'canceled', 'trial')")
  private String status = "active";

  @Column(name = "trial_ends_at")
  private Timestamp trialEndsAt;

  @Column(name = "max_users", nullable = true)
  private Integer maxUsers = 10;

  @Column(name = "max_patients", nullable = true)
  private Integer maxPatients = 100;

  @Column(name = "billing_email", nullable = false)
  private String billingEmail;

  /*
   * Other Approach:
   *
   * @Type(value = JsonBinaryType.class)
   * @JdbcTypeCode(SqlTypes.JSON)
   *
   * Or:
   *
   * @JdbcTypeCode(SqlTypes.JSON)
   * @Column(columnDefinition = "jsonb")
   *
   * */
  @Convert(converter = JsonAttributeConverter.class)
  @Column(name = "settings", nullable = true, columnDefinition = "jsonb")
  private SettingsData settings = new SettingsData();

}
