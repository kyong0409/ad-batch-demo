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
    dag_id='daily_ad_stats_batch',
    default_args=default_args,
    description='Daily ad performance aggregation batch job',
    schedule=None,
    catchup=False,
    is_paused_upon_creation=True,
    tags=['ad-batch', 'demo'],
) as dag:

    run_batch_job = KubernetesPodOperator(
        task_id='run_daily_ad_stats',
        name='daily-ad-stats-batch',
        namespace='ad-batch',
        image='ad-batch-demo:latest',
        image_pull_policy='Never',
        env_vars={
            'SPRING_PROFILES_ACTIVE': 'k8s',
        },
        on_finish_action='delete_succeeded_pod',
        get_logs=True,
        startup_timeout_seconds=300,
        container_resources={
            'limits': {'memory': '512Mi', 'cpu': '500m'},
            'requests': {'memory': '256Mi', 'cpu': '250m'},
        },
    )
