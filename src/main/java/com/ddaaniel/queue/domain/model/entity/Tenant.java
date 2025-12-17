package com.ddaaniel.queue.domain.model.entity;

import java.time.LocalDateTime;

import org.hibernate.annotations.Check;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.type.SqlTypes;

import com.ddaaniel.queue.domain.model.dto.SettingsData;
import com.ddaaniel.queue.domain.model.enuns.TenantStatus;
import com.ddaaniel.queue.domain.model.enuns.TenantSubscriptionPlan;
import com.ddaaniel.queue.service.mapper.JsonAttributeConverter;

import io.hypersistence.utils.hibernate.type.json.JsonBinaryType;
import io.hypersistence.utils.hibernate.type.json.JsonType;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;

@Entity
@Table(name = "tb_tenant")
public class Tenant {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "tenant_id")
  private Long tenantId;

  @Column(name = "tenant_name", nullable = false)
  private String tenantName;

  @Column(name = "domain", unique = true, nullable = false)
  private String domain;

  @Enumerated(EnumType.STRING)
  @Column(name = "subscription_plan", length = 50, nullable = false)
  private TenantSubscriptionPlan subscriptionPlan = TenantSubscriptionPlan.FREE;

  @Enumerated(EnumType.STRING)
  @Column(name = "status", length = 20, nullable = false)
  private TenantStatus status = TenantStatus.ACTIVE;

  @Column(name = "trial_ends_at")
  private LocalDateTime trialEndsAt;

  @Column(name = "max_users", nullable = true)
  private Integer maxUsers;

  @Column(name = "max_patients", nullable = true)
  private Integer maxPatients;

  @Column(name = "billing_email", nullable = false, unique = true)
  private String billingEmail;

  @Column(name = "deleted_at", nullable = true)
  private LocalDateTime deletedAt;

  @CreationTimestamp
  @Column(name = "created_at", nullable = false)
  private LocalDateTime createdAt;

  @UpdateTimestamp
  @Column(name = "updated_at", nullable = false)
  private LocalDateTime updatedAt;

  /*
   * Other Approach:
   *
   * @Type(value = JsonBinaryType.class)
   * @JdbcTypeCode(SqlTypes.JSON)
   *
   * Or:
   *
   * @Convert(converter = JsonAttributeConverter.class)
   * @Column(name = "settings", nullable = true, columnDefinition = "jsonb")
   *
   */
  @JdbcTypeCode(SqlTypes.JSON)
  @Column(name = "settings", nullable = true, columnDefinition = "jsonb")
  private SettingsData settings = new SettingsData();
}
