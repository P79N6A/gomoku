from odps import ODPS
import argparse


def run(args):
    # build an odps instance
    o=ODPS(args.odps_access_id, args.odps_access_key, args.odps_project,endpoint=args.odps_endpoint)
    
    input_table_project = args.odps_project
    input_table_name = args.input_table_name
    if '.' in input_table_name:
        input_table_project = args.input_table_name.split(".")[0]
        input_table_name = args.input_table_name.split(".")[1]
    
    # download data from odps
    input_table = o.get_table(input_table_name, project=input_table_project)
    data = input_table.to_df().to_pandas()
    
    # sample data
    new_data = data.sample(args.sample_row_count)
    
    # create output table and upload data to odps
    o.delete_table(args.output_table_name, if_exists=True)
    output_table_project = args.odps_project
    output_table_name = args.output_table_name
    if '.' in output_table_name:
        output_table_project = args.output_table_name.split(".")[0]
        output_table_name = args.output_table_name.split(".")[1]
        
    table = o.create_table(output_table_name, 
                           input_table.schema,
                           project=output_table_project,
                           if_not_exists=False,
                           lifecycle=3)
    o.write_table(output_table_name, new_data.values.tolist(), project=output_table_project)


if __name__ == '__main__':
    parser = argparse.ArgumentParser(description = '**PAI Helloworld Demo**')
    
    # system args
    parser.add_argument("--ODPSAccessId", dest="odps_access_id", required=True, 
                       help="odps access_id")
    parser.add_argument("--ODPSAccessKey", dest="odps_access_key", required=True,
                       help="odps access_key")
    parser.add_argument("--ODPSProjectName", dest="odps_project", required=True,
                       help="odps project")
    parser.add_argument("--ODPSEndpoint", dest="odps_endpoint", default="http://service-corp.odps.aliyun-inc.com/api",
                       help="odps endpoint")
    parser.add_argument("--userNumber", dest="user_number", default="", 
                       help="user number")
    
    # model args
    parser.add_argument("--inputDataTableName", dest="input_table_name", required=True, type=str, 
                       help="input table name")
    parser.add_argument("--outputDataTableName", dest="output_table_name", required=True, type=str,
                       help="output table name")
    parser.add_argument("--sampleRowCount", dest="sample_row_count", type=int, default=100, 
                       help="sample row count")
    
    args, unkowns = parser.parse_known_args()
    
    run(args)
    
