/**
 * This package contains all the classes for parsing a QRDA III XML file.
 * 
 * Calls to a specific decoder are triggered by a QRDA III template ID with a 
 * simplified set. When a template Id of interest is encountered in the main decoder,
 * it passes control to a decoder with specific knowledge about the node identified. 
 * When control is passed back, the result is used to decide how to proceed in the 
 * document.
 *  
 */
package gov.cms.qpp.conversion.decode;