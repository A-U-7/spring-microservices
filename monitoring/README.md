# Monitoring Stack

## Overview

The monitoring stack provides comprehensive observability for the Spring microservices architecture using Prometheus for metrics collection and Grafana for visualization.

## Components

### Prometheus

- **Purpose**: Metrics collection and alerting
- **Port**: 9090
- **Features**: Time-series database, query language, alerting rules

### Grafana

- **Purpose**: Metrics visualization and dashboards
- **Port**: 3000
- **Features**: Rich dashboards, alerting, multiple data sources

### Alert Rules

- **File**: alert_rules.yml
- **Purpose**: Define alerting conditions
- **Integration**: Prometheus Alertmanager

## Configuration

### Prometheus Configuration (prometheus.yml)

```yaml
global:
  scrape_interval: 15s
  evaluation_interval: 15s

scrape_configs:
  - job_name: 'prometheus'
    static_configs:
      - targets: ['localhost:9090']

  - job_name: 'user-service'
    static_configs:
      - targets: ['user-service:8081']
    metrics_path: '/actuator/prometheus'
    scrape_interval: 5s
```

### Alert Rules (alert_rules.yml)

```yaml
groups:
- name: spring-microservices
  rules:
  - alert: ServiceDown
    expr: up == 0
    for: 1m
    labels:
      severity: critical
    annotations:
      summary: "Service {{ $labels.instance }} is down"
```

### Grafana Datasource (grafana-datasource.yml)

```yaml
apiVersion: 1
datasources:
  - name: Prometheus
    type: prometheus
    access: proxy
    url: http://prometheus:9090
    isDefault: true
```

## Key Metrics

### Service Health

- **Up/Down Status**: `up` metric
- **Response Time**: `http_server_requests_seconds_bucket`
- **Error Rate**: `rate(http_server_requests_seconds_count{status=~"5.."}[5m])`
- **Request Rate**: `rate(http_server_requests_seconds_count[5m])`

### JVM Metrics

- **Memory Usage**: `jvm_memory_used_bytes / jvm_memory_max_bytes * 100`
- **GC Time**: `jvm_gc_collection_seconds_sum`
- **Thread Count**: `jvm_threads_live_threads`
- **Class Loading**: `jvm_classes_loaded_classes`

### Database Metrics

- **Connection Pool**: `hikaricp_connections_active`
- **Query Time**: `hikaricp_connections_timeout_total`
- **Connection Usage**: `hikaricp_connections_usage_seconds_bucket`

### Custom Metrics

- **Business Metrics**: Order count, user registrations
- **Performance Metrics**: API response times
- **Error Metrics**: Specific error codes and messages

## Dashboards

### Spring Microservices Dashboard

The included dashboard (grafana-dashboard.json) provides:
- Service status overview
- HTTP request metrics
- JVM memory usage
- Response time percentiles

### Dashboard Panels

- **Service Status**: UP/DOWN indicators
- **Request Rate**: Requests per second
- **Memory Usage**: JVM heap and non-heap
- **Response Time**: P50, P95, P99 latencies

## Alerting

### Alert Rules

- **ServiceDown**: Service is not responding
- **HighMemoryUsage**: Memory usage above 80%
- **HighCPUUsage**: CPU usage above 80%
- **HighErrorRate**: Error rate above 10%
- **SlowResponseTime**: P95 response time above 2s

### Alert Channels

- Email notifications
- Slack integration
- Webhook notifications
- PagerDuty integration

## Setup Instructions

### Docker Compose Setup

```bash
# Start monitoring stack
docker-compose up prometheus grafana

# Access Grafana
curl http://localhost:3000 (admin/admin)

# Access Prometheus
curl http://localhost:9090
```

### Manual Setup

```bash
# Start Prometheus
docker run -d \
  --name prometheus \
  -p 9090:9090 \
  -v ./monitoring/prometheus.yml:/etc/prometheus/prometheus.yml \
  prom/prometheus

# Start Grafana
docker run -d \
  --name grafana \
  -p 3000:3000 \
  -v grafana-storage:/var/lib/grafana \
  grafana/grafana
```

## Query Examples

### Service Health

```promql
# Get service status
up{job="user-service"}

# Get service response time
histogram_quantile(0.95, http_server_requests_seconds_bucket{job="user-service"})

# Get error rate
rate(http_server_requests_seconds_count{job="user-service",status=~"5.."}[5m])
```

### JVM Metrics

```promql
# Memory usage
jvm_memory_used_bytes{job="user-service",area="heap"} / jvm_memory_max_bytes{job="user-service",area="heap"} * 100

# GC collection time
rate(jvm_gc_collection_seconds_sum{job="user-service"}[5m])
```

### Business Metrics

```promql
# Order creation rate
rate(order_created_total[5m])

# User registration rate
rate(user_registered_total[5m])
```

## Configuration

### Prometheus Configuration

- **Scrape Interval**: How often to collect metrics
- **Evaluation Interval**: How often to evaluate rules
- **Scrape Timeout**: Timeout for metric collection
- **Target Configuration**: Service endpoints to monitor

### Grafana Configuration

- **Datasource**: Prometheus connection settings
- **Dashboard**: Visualization configuration
- **Alerts**: Alert rule configuration
- **Users**: Access control and permissions

## Best Practices

### Metric Collection

- Use appropriate scrape intervals
- Monitor all critical services
- Include business metrics
- Track error rates and response times

### Alerting

- Set meaningful thresholds
- Avoid alert fatigue
- Include runbook information
- Test alert rules regularly

### Dashboard Design

- Focus on key metrics
- Use appropriate visualizations
- Include context and explanations
- Keep dashboards up to date

## Troubleshooting

### Common Issues

- **Metrics not appearing**: Check scrape configuration
- **High memory usage**: Adjust retention settings
- **Slow queries**: Optimize query performance
- **Alert not firing**: Check alert rule configuration

### Debug Commands

```bash
# Check Prometheus targets
curl http://localhost:9090/api/v1/targets

# Check Prometheus configuration
curl http://localhost:9090/api/v1/status/config

# Test query
curl 'http://localhost:9090/api/v1/query?query=up'
```

### Log Analysis

```bash
# Check Prometheus logs
docker logs prometheus

# Check Grafana logs
docker logs grafana
```

## Performance Tuning

### Prometheus

- **Retention**: Adjust storage retention period
- **Memory**: Configure appropriate memory limits
- **Storage**: Use SSD storage for better performance
- **Sharding**: Consider federation for large deployments

### Grafana

- **Caching**: Enable query caching
- **Database**: Use external database for large deployments
- **Resources**: Allocate sufficient memory and CPU

## Security

### Prometheus Security

- Enable authentication
- Use HTTPS for communication
- Implement network security policies
- Regular security updates

### Grafana Security

- Strong authentication
- Role-based access control
- Secure datasource connections
- Regular security audits

## Maintenance

### Regular Tasks

- Update dashboard queries
- Review alert thresholds
- Clean up old data
- Update monitoring rules
- Test alert channels

### Backup

- Backup Prometheus data
- Export Grafana dashboards
- Document configuration changes
- Maintain runbooks

## Integration

### Service Integration

All services should expose metrics at `/actuator/prometheus`:

```yaml
management:
  endpoints:
    web:
      exposure:
        include: health,info,metrics,prometheus
```

### Custom Metrics

Services can expose custom metrics:

```java
@Component
public class BusinessMetrics {
    private final MeterRegistry meterRegistry;

    public void recordOrderCreated() {
        meterRegistry.counter("order.created").increment();
    }
}
```

## Support

For monitoring-related issues:
- Check service metrics endpoints
- Verify Prometheus configuration
- Check Grafana datasource connection
- Review alert rules and thresholds
- Consult Prometheus and Grafana documentation