from airflow import DAG
from airflow.providers.cncf.kubernetes.operators.pod import KubernetesPodOperator
from datetime import datetime, timedelta

default_args = {
    'owner': 'ad-batch-demo',
    'depends_on_past': False,
    'start_date': datetime(2026, 4, 1),
    'retries': 1,
    'retry_delay': timedelta(minutes=5),
}

with DAG(
    dag_id='enterprise_ad_stats_batch',
    default_args=default_args,
    description='Enterprise-grade daily ad stats aggregation with partitioning and CSV export',
    schedule=None,
    catchup=False,
    is_paused_upon_creation=True,
    tags=['ad-batch', 'demo', 'enterprise'],
) as dag:

    run_enterprise_batch = KubernetesPodOperator(
        task_id='run_enterprise_ad_stats',
        name='enterprise-ad-stats-batch',
        namespace='ad-batch',
        image='enterprise-batch:latest',
        image_pull_policy='Never',
        env_vars={
            'SPRING_PROFILES_ACTIVE': 'k8s',
            'TARGET_DATE': '{{ ds }}',
        },
        on_finish_action='delete_succeeded_pod',
        get_logs=True,
        startup_timeout_seconds=600,
        container_resources={
            'limits': {'memory': '1Gi', 'cpu': '1'},
            'requests': {'memory': '512Mi', 'cpu': '500m'},
        },
    )
