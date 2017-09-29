#!/usr/bin/env python

import sys
import csv
import json


def read_apm_entity_ids(file_to_parse_path):
    """
    Returns a list of CPC+ APM Entity IDs given a path to an APM Entity ID data file.

    :param file_to_parse_path: a path to a file
    :return: a list of APM Entity IDs
    """

    apm_entity_id_list = list()

    with open(file_to_parse_path, encoding='mac-roman') as apm_data_file:
        apm_data_parser = csv.DictReader(apm_data_file, delimiter='|', fieldnames=['identifier', 'apm_id', 'subdivision_id',
                                                                                   'apm_entity_id', 'entity_tin', 'entity_name'])

        for row in apm_data_parser:
            apm_entity_id = row['apm_entity_id']

            if row['identifier'] != 'D' or row['apm_id'] != '22' or apm_entity_id is None or len(apm_entity_id) == 0:
                continue

            apm_entity_id_list.append(apm_entity_id)

    return apm_entity_id_list


def write_apm_entity_ids(apm_entity_id_list):
    """
    Writes out the list of APM Entity IDs as JSON to apm_entity.json in the present working directory.

    :param apm_entity_id_list: A list of (hopefully) APM Entity IDs.
    :return:
    """

    with open('apm_entity_ids.json', 'w') as json_file:
        json.dump(apm_entity_id_list, json_file)


if __name__ == '__main__':

    if len(sys.argv) < 2:
        print('No file supplied.')
        exit(1)

    file_to_parse_path = sys.argv[1]

    apm_entity_id_list = read_apm_entity_ids(file_to_parse_path)

    write_apm_entity_ids(apm_entity_id_list)

    print('Parsed and wrote {} APM Entity IDs'.format(len(apm_entity_id_list)))
