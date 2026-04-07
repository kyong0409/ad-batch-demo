from airflow import DAG
from airflow.providers.cncf.kubernetes.operators.pod import KubernetesPodOperator
from datetime import datetime, timedelta

default_args = {
    'owner': 'ad-batch-demo',
    'depends_on_past': False,
    'start_date': datetime(2026, 4, 1),
    'retries': 0,
}

with DAG(
    dag_id='enterprise_order_sync_batch',
    default_args=default_args,
    description='Enterprise order sync batch (failure demonstration with skip policy)',
    schedule=None,
    catchup=False,
    is_paused_upon_creation=True,
    tags=['ad-batch', 'demo', 'enterprise', 'failing'],
) as dag:

    run_order_sync = KubernetesPodOperator(
        task_id='run_order_sync',
        name='enterprise-order-sync-batch',
        namespace='ad-batch',
        image='kyong0409/enterprise-failing-batch:latest',
        image_pull_policy='Always',
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
