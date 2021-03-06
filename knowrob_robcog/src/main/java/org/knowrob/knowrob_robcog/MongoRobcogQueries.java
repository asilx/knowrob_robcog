/*
  Copyright (C) 2014-17 by Andrei Haidu

  Redistribution and use in source and binary forms, with or without
  modification, are permitted provided that the following conditions are met:
      * Redistributions of source code must retain the above copyright
        notice, this list of conditions and the following disclaimer.
      * Redistributions in binary form must reproduce the above copyright
        notice, this list of conditions and the following disclaimer in the
        documentation and/or other materials provided with the distribution.
      * Neither the name of the <organization> nor the
        names of its contributors may be used to endorse or promote products
        derived from this software without specific prior written permission.

  THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
  ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
  WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
  DISCLAIMED. IN NO EVENT SHALL <COPYRIGHT HOLDER> BE LIABLE FOR ANY
  DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
  (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
  LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
  ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
  (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
  SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

  @author Andrei Haidu
  @license BSD
*/

package org.knowrob.knowrob_robcog;

import java.util.ArrayList;
import java.util.List;
import java.util.Arrays;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Iterator;

import java.security.SecureRandom;
import java.lang.StringBuilder;
import javax.vecmath.Vector3d;

import java.io.IOException;
import java.io.Writer;
import java.io.FileWriter;
import java.io.FileOutputStream;
import java.io.BufferedWriter;
import java.io.OutputStreamWriter;
import java.io.File;
import java.io.PrintWriter;
import java.io.RandomAccessFile;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.nio.file.Files;

import com.mongodb.DBObject;
import com.mongodb.BasicDBObject;
import com.mongodb.BasicDBList;
import com.mongodb.Cursor;
import com.mongodb.AggregationOptions;

import org.knowrob.vis.MarkerObject;
import org.knowrob.vis.MarkerPublisher;

import visualization_msgs.Marker;
import geometry_msgs.Point;

public class MongoRobcogQueries {
	
	// marker unique ids
	private Deque<String> markerIDs;
	
	// marker unique ids
	private Deque<String> skeletalMeshMarkerIDs;
	
	// unreal connection to mongodb
	private MongoRobcogConn MongoRobcogConn;

	
	////////////////////////////////////////////////////////////////
	///// MONGO CONNECTION
	/**
	 * MongoRobcogQueries constructor with new mongo connection
	 */	
	public MongoRobcogQueries() {
		// set the connection to unreal
		MongoRobcogConn MongoRobcogConn = new MongoRobcogConn();
		
		// init marker array ids
		this.markerIDs = new ArrayDeque<String>();
		this.skeletalMeshMarkerIDs = new ArrayDeque<String>();
	}
	
	/**
	 * MongoRobcogQueries constructor with copied mongo connection
	 */	
	public MongoRobcogQueries(MongoRobcogConn MongoRobcogConn) {
		// set the connection to unreal
		this.MongoRobcogConn = MongoRobcogConn;
		
		// init marker array ids
		this.markerIDs = new ArrayDeque<String>();
		this.skeletalMeshMarkerIDs = new ArrayDeque<String>();
	}
	
	
	////////////////////////////////////////////////////////////////
	///// HELPER FUNCTIONS	
	/**
	 * Generate a random string
	 */    
	public String randString(int length) {
		final String str_coll = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
		SecureRandom sec_rand =  new SecureRandom();		
		StringBuilder str_builder = new StringBuilder(length);
		for(int i = 0; i < length; ++i){
			str_builder.append(str_coll.charAt(sec_rand.nextInt(str_coll.length())));
		}
		return str_builder.toString();		
	}
	
	/**
	 * Get color vector from string
	 */    
	public float[] colorFromString (String color) {
	     if(color.equals("blue")){
	    	return new float[] {0.0f, 0.0f, 1.0f, 1.0f};	    	
	     }
	     else if(color.equals("lime")){
	    	 return new float[] {0.0f, 1.0f, 0.0f, 1.0f};
	     }
	     else if(color.equals("green")){
	    	 return new float[] {0.0f, 0.5f, 0.0f, 1.0f};
	     }
	     else if(color.equals("yellow")){
	    	 return new float[] {1.0f, 1.0f, 0.0f, 1.0f};
	     }
	     else if(color.equals("orange")){
	    	 return new float[] {1.0f, 0.65f, 0.0f, 1.0f};
	     }
	     else if(color.equals("cyan")){
	    	 return new float[] {0.0f, 1.0f, 1.0f, 1.0f};
	     }
	     else if(color.equals("purple")){
	    	 return new float[] {0.5f, 0.0f, 0.5f, 1.0f};
	     }
	     else if(color.equals("teal")){
	    	 return new float[] {0.0f, 0.5f, 0.5f, 1.0f};
	     }
	     else if(color.equals("magenta")){
	    	 return new float[] {1.0f, 0.0f, 1.0f, 1.0f};
	     }
	     else if(color.equals("brown")){
	    	 return new float[] {0.65f, 0.16f, 0.16f, 1.0f};
	     }
	     else if(color.equals("gray") || color.equals("grey")){
	    	 return new float[] {0.5f, 0.5f, 0.5f, 1.0f};
	     }
	     else if(color.equals("white")){
	    	 return new float[] {1.0f, 1.0f, 1.0f, 1.0f};
	     }
	     else if(color.equals("black")){
	    	 return new float[] {0.0f, 0.0f, 0.0f, 1.0f};
	     }
	     else if(color.equals("red")){
	    	 return new float[] {1.0f, 0.0f, 0.0f, 1.0f};
	     }
	     else{
	    	 return new float[] {1.0f, 0.0f, 0.0f, 1.0f}; // default red       
	     }
	}
	
	/**
	 * Get marker type vector from string
	 */    
	public byte markerFromString (String type) {
	     if(type.equals("sphere")){
	    	return Marker.SPHERE_LIST;	    	
	     }
	     else if(type.equals("cube")){
	    	 return Marker.CUBE_LIST;	
	     }
	     else if(type.equals("point")){
	    	 return Marker.POINTS;
	     }
	     else{
	    	 return Marker.POINTS; // default points      
	     }
	}
	
	/**
	 * Helper function wich parses String with knowrob time format 'timepoint_%d'
	 */
	private double parseTime_d(String timepoint) {
		String x[] = timepoint.split("timepoint_");
		// Also allow input strings without 'timepoint_' prefix
		String ts = (x.length==1 ? x[0] : x[1]);
		return Double.valueOf(ts.replaceAll("[^0-9.]", ""));
	}
	
	/**
	 * Helper function to create a geometry_msgs.Point
	 */
	private Point msgPoint(double x, double y, double z){
		// TODO make node a class attribute?
		Point p = MarkerPublisher.get().getNode().getTopicMessageFactory().newFromType(Point._TYPE);
		p.setX(x);
		p.setY(y);
		p.setZ(z);
		return p;
	}
	
	/**
	 * Helper function to create a geometry_msgs.Point
	 */
	private Point msgPoint(Vector3d v){
		// TODO make node a class attribute?
		Point p = MarkerPublisher.get().getNode().getTopicMessageFactory().newFromType(Point._TYPE);
		p.setX(v.x);
		p.setY(v.y);
		p.setZ(v.z);
		return p;
	}
	
	
	////////////////////////////////////////////////////////////////
	///// MARKER FUNCTIONS	
	/**
	 * Create the rviz markers
	 */
	public void CreateMarkers(ArrayList<Vector3d> pointsArr, String markerID, String markerType, String color, float scale){
		// List of marker points
		List<Point> marker_points = new ArrayList<Point>();
		
		// iterate the 3d vector to create the marker points
		for (Vector3d p_iter : pointsArr){
			marker_points.add(this.msgPoint(p_iter));
		}

		// check if marker already exists
		MarkerObject m = MarkerPublisher.get().getMarker(markerID);
		if(m==null) {			
			// create marker
			m = MarkerPublisher.get().createMarker(markerID);
			// set the type of the marker
			m.setType(markerFromString(markerType));
			// set the positions of the list markers
			m.getMessage().setPoints(marker_points);
			// transform string color to float[] array
			m.setColor(this.colorFromString(color));
			// set the scale of the marker
			m.setScale(new float[] {scale, scale, scale});
		}
		// add ID to the marker container
		this.markerIDs.add(markerID);
	}
	
	/**
	 * Create the rviz mesh marker
	 */
	public void CreateMeshMarker(double pose[], String markerID, String meshPath){		
		// split pose into translation and orientation
		final double[] translation = new double[] {pose[0], pose[1], pose[2]};
		final double[] orientation = new double[] {pose[3], pose[4], pose[5], pose[6]};		
		
		// check if marker already exists
		MarkerObject m = MarkerPublisher.get().getMarker(markerID);
		if(m==null) {
			// create marker
			m = MarkerPublisher.get().createMarker(markerID);
			// set the type of the marker
			m.setType(Marker.MESH_RESOURCE);
			// set the path to the mesh
			m.setMeshResource(meshPath);
			// set pos and rotation
			m.setTranslation(translation);
			m.setOrientation(orientation);
			// set scale
			m.setScale(new float[] {1.0f,1.0f,1.0f});
		}
		// add ID to the marker container
		this.markerIDs.add(markerID);
	}

	/**
	 * Create the bones rviz mesh marker
	 */
	public void CreateBonesMeshMarkers(
			double[][] poses,
			String[] names,
			String markerID,
			String meshFolderPath){
		// create marker for every link mesh
		for (int i = 0; i < names.length; ++i)
		{
			// split pose into translation and orientation
			final double[] translation = new double[] {poses[i][0], poses[i][1], poses[i][2]};
			final double[] orientation = new double[] {poses[i][3], poses[i][4], poses[i][5], poses[i][6]};
			
			final String curr_name = names[i];
			final String curr_id = markerID + curr_name;
			// check if marker already exists (ID + link names)
			MarkerObject m = MarkerPublisher.get().getMarker(curr_id);
			if(m==null) {
				// create marker
				m = MarkerPublisher.get().createMarker(curr_id);
				// set the type of the marker
				m.setType(Marker.MESH_RESOURCE);
				// set the path to the mesh
				m.setMeshResource(meshFolderPath + curr_name + ".dae");
				// set pos and rotation
				m.setTranslation(translation);
				m.setOrientation(orientation);
				// set scale
				m.setScale(new float[] {1.0f,1.0f,1.0f});
			}
			// add ID to the marker container
			this.markerIDs.add(curr_id);		
		}
	}
	
	/**
	 * Create the bones rviz mesh marker
	 */
	public void CreateSkeletalMeshMarkers(
			double[][] poses,
			String[] names,
			String markerID,
			String meshFolderPath){
		// create marker for every link mesh
		for (int i = 0; i < names.length; ++i)
		{
			// split pose into translation and orientation
			final double[] translation = new double[] {poses[i][0], poses[i][1], poses[i][2]};
			final double[] orientation = new double[] {poses[i][3], poses[i][4], poses[i][5], poses[i][6]};
			
			final String curr_name = names[i];
			final String curr_id = markerID + curr_name;
			// check if marker already exists (ID + link names)
			MarkerObject m = MarkerPublisher.get().getMarker(curr_id);
			if(m==null) {
				// create marker
				m = MarkerPublisher.get().createMarker(curr_id);
				// set the type of the marker
				m.setType(Marker.MESH_RESOURCE);
				// set the path to the mesh
				m.setMeshResource(meshFolderPath + curr_name + ".dae");
				// set pos and rotation
				m.setTranslation(translation);
				m.setOrientation(orientation);
				// set scale
				m.setScale(new float[] {1.0f,1.0f,1.0f});
			}
			// add ID to the marker container
			this.skeletalMeshMarkerIDs.add(curr_id);		
		}
	}
	
	/**
	 * Remove the rviz marker with the given ID
	 */
	public void RemoveMarker(String markerID){
		MarkerPublisher.get().eraseMarker(markerID);		
	}
	
	/**
	 * Remove all rviz markers created form sg
	 */
	public void RemoveAllMarkers(){
		// Iterate and remove all markers
        Iterator m_itr = this.markerIDs.iterator();
        while (m_itr.hasNext()) {
                this.RemoveMarker((String)m_itr.next());
        }
	}
	
	/**
	 * Remove all skeletal mesh rviz markers created form sg
	 */
	public void RemoveAllSkeletalMeshMarkers(){
		// Iterate and remove all markers
        Iterator m_itr = this.skeletalMeshMarkerIDs.iterator();
        while (m_itr.hasNext()) {
                this.RemoveMarker((String)m_itr.next());
        }
	}
	
	////////////////////////////////////////////////////////////////
	///// GET QUERY FUNCTIONS
	/**
	 * Query the Pose of the actor at the given timepoint (or the most recent one)
	 */
	public double[] GetActorPoseAt(String actorName, String timestampStr){
		// transform the knowrob time to double with 3 decimal precision
		final double timestamp = (double) Math.round(parseTime_d(timestampStr) * 1000) / 1000;

		return GetActorPoseAt(actorName, timestamp);
	}
	
	/**
	 * Query the Pose of the actor at the given timepoint (or the most recent one)
	 */
	public double[] GetActorPoseAt(String actorName, double timestamp){
		// $and list for querying the $match in the aggregation
		BasicDBList time_and_name = new BasicDBList();

		// add the timestamp and the actor name
		time_and_name.add(new BasicDBObject("timestamp", new BasicDBObject("$lte", timestamp)));
		time_and_name.add(new BasicDBObject("entities.id", actorName));

		// create the pipeline operations, first the $match
		DBObject match_time_and_name = new BasicDBObject(
				"$match", new BasicDBObject( "$and", time_and_name)); 

		// sort the results in descending order on the timestamp (keep most recent result first)
		DBObject sort_desc = new BasicDBObject(
				"$sort", new BasicDBObject("timestamp", -1));

		// $limit the result to 1, we only need one pose
		DBObject limit_result = new BasicDBObject("$limit", 1);

		// $unwind actors in order to output only the queried actor
		DBObject unwind_actors = new BasicDBObject("$unwind", "$entities");

		// $match for the given actor name from the unwinded actors
		DBObject match_actor = new BasicDBObject(
				"$match", new BasicDBObject("entities.id", actorName));

		// build the $projection operation
		DBObject proj_fields = new BasicDBObject("_id", 0);
		proj_fields.put("timestamp", 1);
		proj_fields.put("pos", "$entities.loc");
		proj_fields.put("rot", "$entities.rot");
		DBObject project = new BasicDBObject("$project", proj_fields);

		// run aggregation
		List<DBObject> pipeline = Arrays.asList(
				match_time_and_name, sort_desc, limit_result, unwind_actors, match_actor, project);

		AggregationOptions aggregationOptions = AggregationOptions.builder()
				.batchSize(100)
				.outputMode(AggregationOptions.OutputMode.CURSOR)
				.allowDiskUse(true)
				.build();

		// get results
		Cursor cursor = this.MongoRobcogConn.coll.aggregate(pipeline, aggregationOptions);

		// if query has a response, return the pose
		if(cursor.hasNext())
		{
			// get the first document as the next cursor and append the metadata to it
			BasicDBObject first_doc = (BasicDBObject) cursor.next();			
			// close cursor
			cursor.close();
			// get the pose
			return new double[] {
					((BasicDBObject) first_doc.get("loc")).getDouble("x"),
					((BasicDBObject) first_doc.get("loc")).getDouble("y"),
					((BasicDBObject) first_doc.get("loc")).getDouble("z"),
					((BasicDBObject) first_doc.get("rot")).getDouble("x"),
					((BasicDBObject) first_doc.get("rot")).getDouble("y"),
					((BasicDBObject) first_doc.get("rot")).getDouble("z"),
					((BasicDBObject) first_doc.get("rot")).getDouble("w")};
		}
		else
		{
			System.out.println("Java - GetActorPose - No results found, returning empty list..");			
			return new double[0];
		}
	}

	/**
	 * Query the Traj of the actor between the given timepoints
	 */
	public double[][] GetActorTraj(String actorName,
			String start,
			String end,
			double deltaT){
		// transform the knowrob time to double with 3 decimal precision
		final double start_ts = (double) Math.round(parseTime_d(start) * 1000) / 1000;
		final double end_ts = (double) Math.round(parseTime_d(end) * 1000) / 1000;
		
		return GetActorTraj(actorName, start_ts, end_ts, deltaT);
	}
	
	/**
	 * Query the Traj of the actor between the given timepoints
	 */
	public double[][] GetActorTraj(String actorName,
			double start,
			double end,
			double deltaT){
		// create the pipeline operations, first with the $match check the times
		DBObject match_time = new BasicDBObject("$match", new BasicDBObject("timestamp", 
				new BasicDBObject("$gte", start).append("$lte", end)));

		// $unwind the actors
		DBObject unwind_actors = new BasicDBObject("$unwind", "$entities");

		// $match for the given actor name from the unwinded actors
		DBObject match_actor = new BasicDBObject(
				"$match", new BasicDBObject("entities.id", actorName));

		// build the $projection operation
		DBObject proj_fields = new BasicDBObject("_id", 0);
		proj_fields.put("timestamp", 1);
		proj_fields.put("pos", "$entities.loc");
		proj_fields.put("rot", "$entities.rot");
		DBObject project = new BasicDBObject("$project", proj_fields);

		// run aggregation
		List<DBObject> pipeline = Arrays.asList(match_time, unwind_actors, match_actor, project);

		AggregationOptions aggregationOptions = AggregationOptions.builder()
				.batchSize(100)
				.outputMode(AggregationOptions.OutputMode.CURSOR)
				.allowDiskUse(true)
				.build();

		// get results
		Cursor cursor = this.MongoRobcogConn.coll.aggregate(pipeline, aggregationOptions);
		
		// Traj as dynamic array
		ArrayList<double[]> traj_list = new ArrayList<double[]>();
		
		// if the query returned nothing, get the most recent pose
		if(!cursor.hasNext())
		{
			System.out.println("Java - GetActorTraj - No results found, returning most recent pose..");
			// get the most recent pose
			traj_list.add(this.GetActorPoseAt(actorName, start));
			
			// cast from dynamic array to standard array
			return traj_list.toArray(new double[traj_list.size()][7]);	
		}
		
		// timestamp used for deltaT
		double prev_ts = 0;
				
		// while query has a response, return the pose
		while(cursor.hasNext())
		{
			// get the first document as the next cursor and append the metadata to it
			BasicDBObject curr_doc = (BasicDBObject) cursor.next();
			
			// get the curr timestamp
			double curr_ts = curr_doc.getDouble("timestamp");
			
			// if time diff > then deltaT add position to trajectory
			if(curr_ts - prev_ts > deltaT)
			{			
				// get the current pose
				traj_list.add(new double[] {
						((BasicDBObject) curr_doc.get("loc")).getDouble("x"),
						((BasicDBObject) curr_doc.get("loc")).getDouble("y"),
						((BasicDBObject) curr_doc.get("loc")).getDouble("z"),
						((BasicDBObject) curr_doc.get("rot")).getDouble("x"),
						((BasicDBObject) curr_doc.get("rot")).getDouble("y"),
						((BasicDBObject) curr_doc.get("rot")).getDouble("z"),
						((BasicDBObject) curr_doc.get("rot")).getDouble("w")});
				prev_ts = curr_ts;
				//System.out.println(curr_doc.toString());
			}
		}
		// close cursor
		cursor.close();		
		
		// cast from dynamic array to standard array
		return traj_list.toArray(new double[traj_list.size()][7]);
	}
	
	/**
	 * Get the traveled distance of the actor between the given timepoints
	 */
	public double GetActorTraveledDistance(String actorName,
			String start,
			String end,
			double deltaT){
		// transform the knowrob time to double with 3 decimal precision
		final double start_ts = (double) Math.round(parseTime_d(start) * 1000) / 1000;
		final double end_ts = (double) Math.round(parseTime_d(end) * 1000) / 1000;

		return GetActorTraveledDistance(actorName, start_ts, end_ts, deltaT);
	}
		
	/**
	 * Get the traveled distance of the actor between the given timepoints
	 */
	public double GetActorTraveledDistance(String actorName,
			double start,
			double end,
			double deltaT){
		// traveled distance
		double traveled_distance = 0.0;		
		
		// create the pipeline operations, first with the $match check the times
		DBObject match_time = new BasicDBObject("$match", new BasicDBObject("timestamp", 
				new BasicDBObject("$gte", start).append("$lte", end)));

		// $unwind the actors
		DBObject unwind_actors = new BasicDBObject("$unwind", "$actors");

		// $match for the given actor name from the unwinded actors
		DBObject match_actor = new BasicDBObject(
				"$match", new BasicDBObject("entities.id", actorName));

		// build the $projection operation
		DBObject proj_fields = new BasicDBObject("_id", 0);
		proj_fields.put("timestamp", 1);
		proj_fields.put("pos", "$entities.loc");
		proj_fields.put("rot", "$entities.rot");
		DBObject project = new BasicDBObject("$project", proj_fields);

		// run aggregation
		List<DBObject> pipeline = Arrays.asList(match_time, unwind_actors, match_actor, project);

		AggregationOptions aggregationOptions = AggregationOptions.builder()
				.batchSize(100)
				.outputMode(AggregationOptions.OutputMode.CURSOR)
				.allowDiskUse(true)
				.build();

		// get results
		Cursor cursor = this.MongoRobcogConn.coll.aggregate(pipeline, aggregationOptions);
		
		// Traj as dynamic array
		List<double[]> traj_list_xyz = new ArrayList<double[]>();
		
		// if the query returned nothing distance is 0.
		if(!cursor.hasNext())
		{
			System.out.println("Java - GetActorXYTraveledDistance - No results found, returning 0.0 ..");
			return traveled_distance;	
		}
		
		// timestamp used for deltaT
		double prev_ts = 0;
				
		// while query has a response, return the pose
		while(cursor.hasNext())
		{
			// get the first document as the next cursor and append the metadata to it
			BasicDBObject curr_doc = (BasicDBObject) cursor.next();
			
			// get the curr timestamp
			double curr_ts = curr_doc.getDouble("timestamp");
			
			// if time diff > then deltaT add position to trajectory
			if(curr_ts - prev_ts > deltaT)
			{			
				// get the current pose
				traj_list_xyz.add(new double[] {
						((BasicDBObject) curr_doc.get("loc")).getDouble("x"),
						((BasicDBObject) curr_doc.get("loc")).getDouble("y"),
						((BasicDBObject) curr_doc.get("loc")).getDouble("z")});
				prev_ts = curr_ts;
				//System.out.println(curr_doc.toString());
			}
		}
		// close cursor
		cursor.close();
		
		// Iterate and calculate the 3D points distance backwards until the second last point
		for (int i = traj_list_xyz.size() - 1 ; i >= 1 ; i--) {			
			final double x1_x0 = traj_list_xyz.get(i)[0] - traj_list_xyz.get(i-1)[0];
			final double y1_y0 = traj_list_xyz.get(i)[1] - traj_list_xyz.get(i-1)[1];
			final double z1_z0 = traj_list_xyz.get(i)[2] - traj_list_xyz.get(i-1)[2];
			traveled_distance += Math.sqrt((x1_x0 * x1_x0) + (y1_y0 * y1_y0) + (z1_z0 * z1_z0));
		}
		
		return traveled_distance;
	}
	
	/**
	 * Get the traveled distance on the XY plane of the actor between the given timepoints
	 */
	public double GetActorXYTraveledDistance(String actorName,
			String start,
			String end,
			double deltaT){
		// transform the knowrob time to double with 3 decimal precision
		final double start_ts = (double) Math.round(parseTime_d(start) * 1000) / 1000;
		final double end_ts = (double) Math.round(parseTime_d(end) * 1000) / 1000;
		
		return GetActorXYTraveledDistance(actorName, start_ts, end_ts, deltaT);
	}
	
	/**
	 * Get the traveled distance on the XY plane of the actor between the given timepoints
	 */
	public double GetActorXYTraveledDistance(String actorName,
			double start,
			double end,
			double deltaT){
		// traveled distance
		double traveled_distance = 0.0;		
		
		// create the pipeline operations, first with the $match check the times
		DBObject match_time = new BasicDBObject("$match", new BasicDBObject("timestamp", 
				new BasicDBObject("$gte", start).append("$lte", end)));

		// $unwind the actors
		DBObject unwind_actors = new BasicDBObject("$unwind", "$entities");

		// $match for the given actor name from the unwinded actors
		DBObject match_actor = new BasicDBObject(
				"$match", new BasicDBObject("actors.name", actorName));

		// build the $projection operation
		DBObject proj_fields = new BasicDBObject("_id", 0);
		proj_fields.put("timestamp", 1);
		proj_fields.put("pos", "$entities.loc");
		proj_fields.put("rot", "$entities.loc");
		DBObject project = new BasicDBObject("$project", proj_fields);

		// run aggregation
		List<DBObject> pipeline = Arrays.asList(match_time, unwind_actors, match_actor, project);

		AggregationOptions aggregationOptions = AggregationOptions.builder()
				.batchSize(100)
				.outputMode(AggregationOptions.OutputMode.CURSOR)
				.allowDiskUse(true)
				.build();

		// get results
		Cursor cursor = this.MongoRobcogConn.coll.aggregate(pipeline, aggregationOptions);
		
		// Traj as dynamic array
		List<double[]> traj_list_xy = new ArrayList<double[]>();
		
		// if the query returned nothing distance is 0.
		if(!cursor.hasNext())
		{
			System.out.println("Java - GetActorXYTraveledDistance - No results found, returning 0.0 ..");
			return traveled_distance;	
		}
		
		// timestamp used for deltaT
		double prev_ts = 0;
				
		// while query has a response, return the pose
		while(cursor.hasNext())
		{
			// get the first document as the next cursor and append the metadata to it
			BasicDBObject curr_doc = (BasicDBObject) cursor.next();
			
			// get the curr timestamp
			double curr_ts = curr_doc.getDouble("timestamp");
			
			// if time diff > then deltaT add position to trajectory
			if(curr_ts - prev_ts > deltaT)
			{			
				// get the current pose
				traj_list_xy.add(new double[] {
						((BasicDBObject) curr_doc.get("loc")).getDouble("x"),
						((BasicDBObject) curr_doc.get("loc")).getDouble("y")});
				prev_ts = curr_ts;
				//System.out.println(curr_doc.toString());
			}
		}
		// close cursor
		cursor.close();
		
		// Iterate and calculate the 2D points distance backwards until the second last point
		for (int i = traj_list_xy.size() - 1 ; i >= 1 ; i--) {			
			final double x1_x0 = traj_list_xy.get(i)[0] - traj_list_xy.get(i-1)[0];
			final double y1_y0 = traj_list_xy.get(i)[1] - traj_list_xy.get(i-1)[1];			
			traveled_distance += Math.sqrt((x1_x0 * x1_x0) + (y1_y0 * y1_y0));
		}
		
		return traveled_distance;
	}
	
	/**
	 * Query the Pose of the actors bone at the given timepoint (or the most recent one)
	 */
	public double[] GetBonePoseAt(String actorName, String boneName, String timestampStr){
		final double timestamp = (double) Math.round(parseTime_d(timestampStr) * 1000) / 1000;
		return GetBonePoseAt(actorName, boneName, timestamp);
	}
	
	/**
	 * Query the Pose of the actors bone at the given timepoint (or the most recent one)
	 */
	public double[] GetBonePoseAt(String actorName, String boneName, double timestamp){		
		// $and list for querying the $match in the aggregation
		BasicDBList time_and_name = new BasicDBList();

		// add the timestamp and the actor name
		time_and_name.add(new BasicDBObject("timestamp", new BasicDBObject("$lte", timestamp)));
		time_and_name.add(new BasicDBObject("entities.id", actorName));

		// create the pipeline operations, first the $match
		DBObject match_time_and_name = new BasicDBObject(
				"$match", new BasicDBObject( "$and", time_and_name)); 

		// sort the results in descending order on the timestamp (keep most recent result first)
		DBObject sort_desc = new BasicDBObject(
				"$sort", new BasicDBObject("timestamp", -1));

		// $limit the result to 1, we only need one pose
		DBObject limit_result = new BasicDBObject("$limit", 1);

		// $unwind actors in order to output only the queried actor
		DBObject unwind_actors = new BasicDBObject("$unwind", "$entities");

		// $match for the given actor name from the unwinded actors
		DBObject match_actor = new BasicDBObject(
				"$match", new BasicDBObject("entities.id", actorName));

		// build the $projection operation
		DBObject proj_bone_fields = new BasicDBObject("_id", 0);
		proj_bone_fields.put("timestamp", 1);
		proj_bone_fields.put("actors.bones", 1);
		DBObject project_bones = new BasicDBObject("$project", proj_bone_fields);

		
		// $unwind the bones
		DBObject unwind_bones = new BasicDBObject("$unwind", "$skel_entities.bones");

		// $match for the given bone name from the unwinded bones
		DBObject match_bone = new BasicDBObject(
				"$match", new BasicDBObject("skel_entities.bones.name", boneName));

		// build the final $projection operation
		DBObject proj_fields = new BasicDBObject("timestamp", 1);
		proj_fields.put("pos", "$skel_entities.bones.loc");
		proj_fields.put("rot", "$skel_entities.bones.rot");
		DBObject project = new BasicDBObject("$project", proj_fields);

		// run aggregation
		List<DBObject> pipeline = Arrays.asList(
				match_time_and_name, sort_desc, limit_result, unwind_actors, match_actor,
				project_bones, unwind_bones, match_bone, project);
				

		AggregationOptions aggregationOptions = AggregationOptions.builder()
				.batchSize(100)
				.outputMode(AggregationOptions.OutputMode.CURSOR)
				.allowDiskUse(true)
				.build();

		// get results
		Cursor cursor = this.MongoRobcogConn.coll.aggregate(pipeline, aggregationOptions);

		// if query has a response, return the pose
		if(cursor.hasNext())
		{
			// get the first document as the next cursor and append the metadata to it
			BasicDBObject first_doc = (BasicDBObject) cursor.next();	
			// close cursor since we only care about one value
			cursor.close();
			// get the pose
			return new double[] {
					((BasicDBObject) first_doc.get("loc")).getDouble("x"),
					((BasicDBObject) first_doc.get("loc")).getDouble("y"),
					((BasicDBObject) first_doc.get("loc")).getDouble("z"),
					((BasicDBObject) first_doc.get("rot")).getDouble("x"),
					((BasicDBObject) first_doc.get("rot")).getDouble("y"),
					((BasicDBObject) first_doc.get("rot")).getDouble("z"),
					((BasicDBObject) first_doc.get("rot")).getDouble("w")};
		}
		else
		{
			System.out.println("Java - GetBonePose - No results found, returning empty list..");
			return new double[0];
		}
	}
	
	/**
	 * Query the Traj of the actors bone between the given timepoints
	 */
	public double [][] GetBoneTraj(String actorName,
			String boneName,
			String start,
			String end,
			double deltaT){		
		// transform the knowrob time to double with 3 decimal precision
		final double start_ts = (double) Math.round(parseTime_d(start) * 1000) / 1000;
		final double end_ts = (double) Math.round(parseTime_d(end) * 1000) / 1000;

		return GetBoneTraj(actorName, boneName, start_ts, end_ts, deltaT);
	}
	
	/**
	 * Query the Traj of the actors bone between the given timepoints
	 */
	public double [][] GetBoneTraj(String actorName,
			String boneName,
			double start,
			double end,
			double deltaT){		
		
		// create the pipeline operations, first with the $match check the times
		DBObject match_time = new BasicDBObject("$match", new BasicDBObject("timestamp", 
				new BasicDBObject("$gte", start).append("$lte", end)));

		// $unwind the actors
		DBObject unwind_actors = new BasicDBObject("$unwind", "$skel_entities");

		// $match for the given actor name from the unwinded actors
		DBObject match_actor = new BasicDBObject(
				"$match", new BasicDBObject("skel_entities.id", actorName));

		// build the $projection operation
		DBObject proj_fields = new BasicDBObject("_id", 0);
		proj_fields.put("timestamp", 1);
		proj_fields.put("pos", "$skel_entities.loc");
		proj_fields.put("rot", "$skel_entities.rot");
		DBObject project = new BasicDBObject("$project", proj_fields);

		// run aggregation
		List<DBObject> pipeline = Arrays.asList(match_time, unwind_actors, match_actor, project);

		AggregationOptions aggregationOptions = AggregationOptions.builder()
				.batchSize(100)
				.outputMode(AggregationOptions.OutputMode.CURSOR)
				.allowDiskUse(true)
				.build();

		// get results
		Cursor cursor = this.MongoRobcogConn.coll.aggregate(pipeline, aggregationOptions);
		
		// Traj as dynamic array
		ArrayList<double[]> traj_list = new ArrayList<double[]>();	
		
		// if the query returned nothing, get the most recent pose
		if(!cursor.hasNext())
		{
			System.out.println("Java - GetBoneTraj - No results found, returning most recent pose..");
			// get the most recent pose
			traj_list.add(this.GetBonePoseAt(actorName, boneName, start));
			
			// cast from dynamic array to standard array
			return traj_list.toArray(new double[traj_list.size()][7]);	
		}
		
		// timestamp used for deltaT
		double prev_ts = 0;
		
		// if query has a response, return the pose
		while(cursor.hasNext())
		{
			// get the first document as the next cursor and append the metadata to it
			BasicDBObject curr_doc = (BasicDBObject) cursor.next();			
			
			// get the curr timestamp
			double curr_ts = curr_doc.getDouble("timestamp");
			
			// if time diff > then deltaT add position to trajectory
			if(curr_ts - prev_ts > deltaT)
			{
				// get the current pose
				traj_list.add(new double[] {
						((BasicDBObject) curr_doc.get("loc")).getDouble("x"),
						((BasicDBObject) curr_doc.get("loc")).getDouble("y"),
						((BasicDBObject) curr_doc.get("loc")).getDouble("z"),
						((BasicDBObject) curr_doc.get("rot")).getDouble("x"),
						((BasicDBObject) curr_doc.get("rot")).getDouble("y"),
						((BasicDBObject) curr_doc.get("rot")).getDouble("z"),
						((BasicDBObject) curr_doc.get("rot")).getDouble("w")});
				prev_ts = curr_ts;
				//System.out.println(curr_doc.toString());
			}
		}
		// close cursor
		cursor.close();		
		
		// cast from dynamic array to standard array
		return traj_list.toArray(new double[traj_list.size()][7]);
	}
	
	/**
	 * Query the Names of the actor bones
	 */
	public String[] GetBonesNames(String actorName){
		// create the pipeline operations, first the $match
		DBObject match_name = new BasicDBObject(
				"$match", new BasicDBObject("skel_entities.id", actorName)); 

		// $limit the result to 1
		DBObject limit_result = new BasicDBObject("$limit", 1);

		// $unwind actors in order to output only the queried actor
		DBObject unwind_actors = new BasicDBObject("$unwind", "$skel_entities");

		// $match for the given actor name from the unwinded actors
		DBObject match_actor = new BasicDBObject(
				"$match", new BasicDBObject("skel_entities.id", actorName));

		// build the $projection operation
		DBObject proj_fields = new BasicDBObject("_id", 0);
		proj_fields.put("bones_names", "$skel_entities.bones.name");
		DBObject project = new BasicDBObject("$project", proj_fields);

		// run aggregation
		List<DBObject> pipeline = Arrays.asList(
				match_name, limit_result, unwind_actors, match_actor, project);
				

		AggregationOptions aggregationOptions = AggregationOptions.builder()
				.batchSize(100)
				.outputMode(AggregationOptions.OutputMode.CURSOR)
				.allowDiskUse(true)
				.build();

		// get results
		Cursor cursor = this.MongoRobcogConn.coll.aggregate(pipeline, aggregationOptions);
		
		// if query has a response, return the names
		if(cursor.hasNext())
		{
			// get the first document as the next cursor and append the metadata to it
			BasicDBObject first_doc = (BasicDBObject) cursor.next();	
			// close cursor since we only care about one value
			cursor.close();
			// get the bone names as list			
			BasicDBList names = (BasicDBList) first_doc.get("bones_names");			
			// return as array of string
			return names.toArray(new String[names.size()]);
		}
		else // else return empty list
		{
			System.out.println("Java - GetBonesNames - No results found, returning empty list..");
			return new String[0];
		}
	}
	
	/**
	 * Query the Poses of the actor bones at the given timepoint (or the most recent one)
	 */
	public double[][] GetBonesPosesAt(String actorName, String timestampStr){
		// transform the knowrob time to double with 3 decimal precision
		final double timestamp = (double) Math.round(parseTime_d(timestampStr) * 1000) / 1000;

		return GetBonesPosesAt(actorName, timestamp);
	}
	
	/**
	 * Query the Poses of the actor bones at the given timepoint (or the most recent one)
	 */
	public double[][] GetBonesPosesAt(String actorName, double timestamp){
		
		// $and list for querying the $match in the aggregation
		BasicDBList time_and_name = new BasicDBList();

		// add the timestamp and the actor name
		time_and_name.add(new BasicDBObject("timestamp", new BasicDBObject("$lte", timestamp)));
		time_and_name.add(new BasicDBObject("skel_entities.id", actorName));

		// create the pipeline operations, first the $match
		DBObject match_time_and_name = new BasicDBObject(
				"$match", new BasicDBObject( "$and", time_and_name)); 

		// sort the results in descending order on the timestamp (keep most recent result first)
		DBObject sort_desc = new BasicDBObject(
				"$sort", new BasicDBObject("timestamp", -1));

		// $limit the result to 1, we only need one pose
		DBObject limit_result = new BasicDBObject("$limit", 1);

		// $unwind actors in order to output only the queried actor
		DBObject unwind_actors = new BasicDBObject("$unwind", "$skel_entities");

		// $match for the given actor name from the unwinded actors
		DBObject match_actor = new BasicDBObject(
				"$match", new BasicDBObject("skel_entities.id", actorName));

		// build the $projection operation
		DBObject proj_fields = new BasicDBObject("_id", 0);
		proj_fields.put("timestamp", 1);
		proj_fields.put("bones_pos", "$skel_entities.bones.loc");
		proj_fields.put("bones_rot", "$skel_entities.bones.rot");
		DBObject project = new BasicDBObject("$project", proj_fields);

		// run aggregation
		List<DBObject> pipeline = Arrays.asList(
				match_time_and_name, sort_desc, limit_result, unwind_actors, match_actor, project);
				

		AggregationOptions aggregationOptions = AggregationOptions.builder()
				.batchSize(100)
				.outputMode(AggregationOptions.OutputMode.CURSOR)
				.allowDiskUse(true)
				.build();

		// get results
		Cursor cursor = this.MongoRobcogConn.coll.aggregate(pipeline, aggregationOptions);

		// Poses as dynamic array
		ArrayList<double[]> pose_list = new ArrayList<double[]>();	
		
		// if query has a response, return the pose
		if(cursor.hasNext())
		{
			// get the first document as the next cursor and append the metadata to it
			BasicDBObject first_doc = (BasicDBObject) cursor.next();	
			// close cursor since we only care about one value
			cursor.close();
			// get the pose
			
			BasicDBList pos_list = (BasicDBList) first_doc.get("bones_pos");
			BasicDBList rot_list = (BasicDBList) first_doc.get("bones_rot");
			
			// pos_list and rot_list length should be always the same
			for (int i = 0; i < pos_list.size(); ++i)
			{				
				pose_list.add(new double[]{
					((BasicDBObject) pos_list.get(i)).getDouble("x"),
					((BasicDBObject) pos_list.get(i)).getDouble("y"),
					((BasicDBObject) pos_list.get(i)).getDouble("z"),
					((BasicDBObject) rot_list.get(i)).getDouble("x"),
					((BasicDBObject) rot_list.get(i)).getDouble("y"),
					((BasicDBObject) rot_list.get(i)).getDouble("z"),
					((BasicDBObject) rot_list.get(i)).getDouble("w")});		
			}
			// cast from dynamic array to standard array
			return pose_list.toArray(new double[pose_list.size()][7]);
		}
		else
		{
			System.out.println("Java - GetBonesPoses - No results found, returning empty list..");
			return new double[0][0];
		}
	}
	
	/**
	 * Query the Trajectories of the actor bones at the given timepoint (or the most recent one)
	 */
	public double[][][] GetBonesTrajs(String actorName,
			String start,
			String end,
			double deltaT){	
		// transform the knowrob time to double with 3 decimal precision
		final double start_ts = (double) Math.round(parseTime_d(start) * 1000) / 1000;
		final double end_ts = (double) Math.round(parseTime_d(end) * 1000) / 1000;

		return GetBonesTrajs(actorName, start_ts, end_ts, deltaT);
	}

	/**
	 * Query the Trajectories of the actor bones at the given timepoint (or the most recent one)
	 */
	public double[][][] GetBonesTrajs(String actorName,
			double start,
			double end,
			double deltaT){	
		// create the pipeline operations, first with the $match check the times
		DBObject match_time = new BasicDBObject("$match", new BasicDBObject("timestamp", 
				new BasicDBObject("$gte", start).append("$lte", end)));

		// $unwind actors in order to output only the queried actor
		DBObject unwind_actors = new BasicDBObject("$unwind", "$skel_entities");

		// $match for the given actor name from the unwinded actors
		DBObject match_actor = new BasicDBObject(
				"$match", new BasicDBObject("skel_entities.id", actorName));

		// build the $projection operation
		DBObject proj_fields = new BasicDBObject("_id", 0);
		proj_fields.put("timestamp", 1);
		proj_fields.put("bones_pos", "$skel_entities.bones.loc");
		proj_fields.put("bones_rot", "$skel_entities.bones.rot");
		DBObject project = new BasicDBObject("$project", proj_fields);

		// run aggregation
		List<DBObject> pipeline = Arrays.asList(
				match_time, unwind_actors, match_actor, project);				

		AggregationOptions aggregationOptions = AggregationOptions.builder()
				.batchSize(100)
				.outputMode(AggregationOptions.OutputMode.CURSOR)
				.allowDiskUse(true)
				.build();

		// get results
		Cursor cursor = this.MongoRobcogConn.coll.aggregate(pipeline, aggregationOptions);

		// Trajectories as dynamic array (dynamic on the time part)
		ArrayList<double[][]> bone_trajs = new ArrayList<double[][]>();
		
		// the number of bones
		int nr_bones = 0;
		
		// if the query returned nothing, get the most recent pose
		if(!cursor.hasNext())
		{
			System.out.println("Java - GetBonesTrajs - No results found, returning most recent poses..");
			// get the most recent pose
			bone_trajs.add(this.GetBonesPosesAt(actorName, start));
			
			// set the nr of bones
			nr_bones = bone_trajs.get(0).length;
			
			// cast from dynamic array to standard array
			return bone_trajs.toArray(new double[bone_trajs.size()][nr_bones][7]);
		}
		
		// timestamp used for deltaT
		double prev_ts = 0;
		
		// if query has a response, return the pose
		while(cursor.hasNext())
		{
			// get the first document as the next cursor and append the metadata to it
			BasicDBObject curr_doc = (BasicDBObject) cursor.next();

			// get the curr timestamp
			double curr_ts = curr_doc.getDouble("timestamp");
			
			// if time diff > then deltaT add position to trajectory
			if(curr_ts - prev_ts > deltaT)
			{
				// get the list of bones pos and rot
				BasicDBList pos_list = (BasicDBList) curr_doc.get("bones_pos");
				BasicDBList rot_list = (BasicDBList) curr_doc.get("bones_rot");
				
				// set the nr of bones
				nr_bones = pos_list.size();
				
				// Poses as dynamic array (dynamic on the nr of bones part)
				ArrayList<double[]> pose_list = new ArrayList<double[]>();
				
				// pos_list and rot_list length should be always the same
				for (int i = 0; i < nr_bones; ++i)
				{				
					pose_list.add(new double[]{
							((BasicDBObject) pos_list.get(i)).getDouble("x"),
							((BasicDBObject) pos_list.get(i)).getDouble("y"),
							((BasicDBObject) pos_list.get(i)).getDouble("z"),
							((BasicDBObject) rot_list.get(i)).getDouble("x"),
							((BasicDBObject) rot_list.get(i)).getDouble("y"),
							((BasicDBObject) rot_list.get(i)).getDouble("z"),
							((BasicDBObject) rot_list.get(i)).getDouble("w")});
				}
				// cast from dynamic array to standard array
				bone_trajs.add(pose_list.toArray(new double[nr_bones][7]));				
				prev_ts = curr_ts;
			}
		}
		// close cursor
		cursor.close();	
		
		// return the actor bones trajectories as float[][][] 
		return bone_trajs.toArray(new double[bone_trajs.size()][nr_bones][7]);
	}
	
	////////////////////////////////////////////////////////////////
	///// VIS QUERY FUNCTIONS	
	/**
	 * View and return the Pose of the actor at the given timepoint (or the most recent one)
	 */
	public void ViewActorPoseAt(String actorName, 
			String timestampStr,
			String markerType,
			String color,
			float scale){
		// gen id
		final String marker_id = actorName + "_" + this.randString(4);
		// create the marker
		this.ViewActorPoseAt(actorName, timestampStr, marker_id, markerType, color, scale);
	}
	
	/**
	 * View and return the Pose of the actor at the given timepoint (or the most recent one)
	 */
	public void ViewActorPoseAt(String actorName, 
			double timestamp,
			String markerType,
			String color,
			float scale){
		// gen id
		final String marker_id = actorName + "_" + this.randString(4);
		// create the marker
		this.ViewActorPoseAt(actorName, timestamp, marker_id, markerType, color, scale);
	}
	
	/**
	 * View and return the Pose of the actor at the given timepoint (or the most recent one)
	 */
	public void ViewActorPoseAt(String actorName, 
			String timestampStr, 
			String markerID,
			String markerType,
			String color,
			float scale){
		// get the pose of the actor
		final double[] pose = this.GetActorPoseAt(actorName, timestampStr);
	
		// return position as arraylist
		ArrayList<Vector3d> pos = new ArrayList<Vector3d>();
		pos.add(new Vector3d(pose[0],pose[1],pose[2]));
		
		// create the marker
		this.CreateMarkers(pos, markerID, markerType, color, scale);
	}
	
	/**
	 * View and return the Pose of the actor at the given timepoint (or the most recent one)
	 */
	public void ViewActorPoseAt(String actorName, 
			double timestamp, 
			String markerID,
			String markerType,
			String color,
			float scale){
		// get the pose of the actor
		final double[] pose = this.GetActorPoseAt(actorName, timestamp);
	
		// return position as arraylist
		ArrayList<Vector3d> pos = new ArrayList<Vector3d>();
		pos.add(new Vector3d(pose[0],pose[1],pose[2]));
		
		// create the marker
		this.CreateMarkers(pos, markerID, markerType, color, scale);
	}
	
	/**
	 * Query the Pose of the given model at the given timepoint (or the most recent one)
	 * view results as rviz mesh marker 
	 */
	public void ViewActorMeshAt(String actorName,			
			String timestampStr,
			String meshPath){
		// gen id
		final String marker_id = actorName + "_" + this.randString(4);
		// create the marker
		this.ViewActorMeshAt(actorName, timestampStr, marker_id, meshPath);
	}
	
	/**
	 * Query the Pose of the given model at the given timepoint (or the most recent one)
	 * view results as rviz mesh marker 
	 */
	public void ViewActorMeshAt(String actorName,			
			double timestamp,
			String meshPath){
		// gen id
		final String marker_id = actorName + "_" + this.randString(4);
		// create the marker
		this.ViewActorMeshAt(actorName, timestamp, marker_id, meshPath);
	}
	
	/**
	 * Query the Pose of the given model at the given timepoint (or the most recent one)
	 * view results as rviz mesh marker 
	 */
	public void ViewActorMeshAt(String actorName,			
			String timestampStr, 
			String markerID,
			String meshPath){
		// get the pose of the actor
		final double[] pose = this.GetActorPoseAt(actorName, timestampStr);
		
		// create mesh marker
		this.CreateMeshMarker(pose, markerID, meshPath);
	}
	
	/**
	 * Query the Pose of the given model at the given timepoint (or the most recent one)
	 * view results as rviz mesh marker 
	 */
	public void ViewActorMeshAt(String actorName,			
			double timestamp, 
			String markerID,
			String meshPath){
		// get the pose of the actor
		final double[] pose = this.GetActorPoseAt(actorName, timestamp);
		
		// create mesh marker
		this.CreateMeshMarker(pose, markerID, meshPath);
	}

	/**
	 * Get the poses of the actorss bones meshes at the given timestamp
	 * view results as rviz markers
	 */
	public void ViewBonesMeshesAt(String actorName,
			String timestampStr,
			String meshFolderPath){	
		// gen id
		final String marker_id = actorName + "_" + this.randString(4);
		// create the marker
		this.ViewBonesMeshesAt(actorName, timestampStr, marker_id, meshFolderPath);
	}
	
	/**
	 * Get the poses of the actorss bones meshes at the given timestamp
	 * view results as rviz markers
	 */
	public void ViewBonesMeshesAt(String actorName,
			double timestamp,
			String meshFolderPath){	
		// gen id
		final String marker_id = actorName + "_" + this.randString(4);
		// create the marker
		this.ViewBonesMeshesAt(actorName, timestamp, marker_id, meshFolderPath);
	}
	
	/**
	 * Get the poses of the actorss bones meshes at the given timestamp
	 * view results as rviz markers
	 */
	public void ViewBonesMeshesAt(String actorName,
			String timestampStr,
			String markerID,
			String meshFolderPath){		
		// get the names of the bones
		final String[] names = this.GetBonesNames(actorName); 
	
		// pos xyz rot wxyz for every bone
		final double[][] bone_poses = this.GetBonesPosesAt(actorName, timestampStr);

		// create the bones mesh markers
		this.CreateBonesMeshMarkers(bone_poses, names, markerID, meshFolderPath);
	}
	
	/**
	 * Get the poses of the actorss bones meshes at the given timestamp
	 * view results as rviz markers
	 */
	public void ViewBonesMeshesAt(String actorName,
			double timestamp,
			String markerID,
			String meshFolderPath){		
		// get the names of the bones
		final String[] names = this.GetBonesNames(actorName); 
	
		// pos xyz rot wxyz for every bone
		final double[][] bone_poses = this.GetBonesPosesAt(actorName, timestamp);

		// create the bones mesh markers
		this.CreateBonesMeshMarkers(bone_poses, names, markerID, meshFolderPath);
	}
	
	/**
	 * Get the poses of the actorss bones meshes at the given timestamp
	 * view results as rviz markers
	 */
	public void ViewSkeletalMeshAt(String actorName,
			String timestampStr,
			String meshFolderPath){	
		// gen id
		final String marker_id = actorName + "_" + this.randString(4);
		// create the marker
		this.ViewSkeletalMeshAt(actorName, timestampStr, marker_id, meshFolderPath);
	}
	
	/**
	 * Get the poses of the actorss bones meshes at the given timestamp
	 * view results as rviz markers
	 */
	public void ViewSkeletalMeshAt(String actorName,
			double timestamp,
			String meshFolderPath){	
		// gen id
		final String marker_id = actorName + "_" + this.randString(4);
		// create the marker
		this.ViewSkeletalMeshAt(actorName, timestamp, marker_id, meshFolderPath);
	}
	
	/**
	 * Get the poses of the actorss bones meshes at the given timestamp
	 * view results as rviz markers
	 */
	public void ViewSkeletalMeshAt(String actorName,
			String timestampStr,
			String markerID,
			String meshFolderPath){		
		// get the names of the bones
		final String[] names = this.GetBonesNames(actorName); 
	
		// pos xyz rot wxyz for every bone
		final double[][] bone_poses = this.GetBonesPosesAt(actorName, timestampStr);

		// create the bones mesh markers
		this.CreateSkeletalMeshMarkers(bone_poses, names, markerID, meshFolderPath);
	}
	
	/**
	 * Get the poses of the actorss bones meshes at the given timestamp
	 * view results as rviz markers
	 */
	public void ViewSkeletalMeshAt(String actorName,
			double timestamp,
			String markerID,
			String meshFolderPath){		
		// get the names of the bones
		final String[] names = this.GetBonesNames(actorName); 
	
		// pos xyz rot wxyz for every bone
		final double[][] bone_poses = this.GetBonesPosesAt(actorName, timestamp);

		// create the bones mesh markers
		this.CreateSkeletalMeshMarkers(bone_poses, names, markerID, meshFolderPath);
	}

	/**
	 * View and return the Pose of the actors bone at the given timepoint (or the most recent one)
	 */
	public void ViewBonePoseAt(String actorName,
			String boneName,
			String timestampStr,
			String markerType,
			String color,
			float scale){	
		// gen id
		final String marker_id = boneName + "_" + this.randString(4);
		// create the marker
		this.ViewBonePoseAt(actorName, boneName, timestampStr, marker_id, markerType, color, scale);
	}
	
	/**
	 * View and return the Pose of the actors bone at the given timepoint (or the most recent one)
	 */
	public void ViewBonePoseAt(String actorName,
			String boneName,
			double timestamp,
			String markerType,
			String color,
			float scale){	
		// gen id
		final String marker_id = boneName + "_" + this.randString(4);
		// create the marker
		this.ViewBonePoseAt(actorName, boneName, timestamp, marker_id, markerType, color, scale);
	}
	
	/**
	 * View and return the Pose of the actors bone at the given timepoint (or the most recent one)
	 */
	public void ViewBonePoseAt(String actorName,
			String boneName,
			String timestampStr, 
			String markerID,
			String markerType,
			String color,
			float scale){	
		// get the pose of the actor
		final double[] pose = this.GetBonePoseAt(actorName, boneName, timestampStr);
	
		// return position as arraylist
		ArrayList<Vector3d> pos = new ArrayList<Vector3d>();
		pos.add(new Vector3d(pose[0],pose[1],pose[2]));
		
		// create the markers
		this.CreateMarkers(pos, markerID, markerType, color, scale);
	}
	
	/**
	 * View and return the Pose of the actors bone at the given timepoint (or the most recent one)
	 */
	public void ViewBonePoseAt(String actorName,
			String boneName,
			double timestamp, 
			String markerID,
			String markerType,
			String color,
			float scale){	
		// get the pose of the actor
		final double[] pose = this.GetBonePoseAt(actorName, boneName, timestamp);
	
		// return position as arraylist
		ArrayList<Vector3d> pos = new ArrayList<Vector3d>();
		pos.add(new Vector3d(pose[0],pose[1],pose[2]));
		
		// create the markers
		this.CreateMarkers(pos, markerID, markerType, color, scale);
	}
	
	/**
	 * View and return the Traj of the actor between the given timepoints
	 */
	public void ViewActorTraj(String actorName,
			String start,
			String end,
			String markerType,
			String color,
			float scale,
			double deltaT){	
		// gen id
		final String marker_id = actorName + "_" + this.randString(4);
		// create the marker
		this.ViewActorTraj(actorName, start, end, marker_id, markerType, color, scale, deltaT);
	}
	
	/**
	 * View and return the Traj of the actor between the given timepoints
	 */
	public void ViewActorTraj(String actorName,
			double start,
			double end,
			String markerType,
			String color,
			float scale,
			double deltaT){	
		// gen id
		final String marker_id = actorName + "_" + this.randString(4);
		// create the marker
		this.ViewActorTraj(actorName, start, end, marker_id, markerType, color, scale, deltaT);
	}
	
	/**
	 * View and return the Traj of the actor between the given timepoints
	 */
	public void ViewActorTraj(String actorName,
			String start,
			String end,
			String markerID,
			String markerType,
			String color,
			float scale,
			double deltaT){		
		// get the trajectory
		final double[][] traj = this.GetActorTraj(actorName, start, end, deltaT);

		// positions as arraylist
		ArrayList<Vector3d> pos = new ArrayList<Vector3d>();

		// add trajectory points
		for (int i = 0; i < traj.length; i++){
			pos.add(new Vector3d(traj[i][0], traj[i][1], traj[i][2]));
		}
		// create the markers
		this.CreateMarkers(pos, markerID, markerType, color, scale);
	}
	
	/**
	 * View and return the Traj of the actor between the given timepoints
	 */
	public void ViewActorTraj(String actorName,
			double start,
			double end,
			String markerID,
			String markerType,
			String color,
			float scale,
			double deltaT){		
		// get the trajectory
		final double[][] traj = this.GetActorTraj(actorName, start, end, deltaT);

		// positions as arraylist
		ArrayList<Vector3d> pos = new ArrayList<Vector3d>();

		// add trajectory points
		for (int i = 0; i < traj.length; i++){
			pos.add(new Vector3d(traj[i][0], traj[i][1], traj[i][2]));
		}
		// create the markers
		this.CreateMarkers(pos, markerID, markerType, color, scale);
	}

	/**
	 * View and return the Pose of the actor between the given timepoints
	 */
	public void ViewBoneTraj(String actorName,
			String boneName,
			String start,
			String end,
			String markerType,
			String color,
			float scale,
			double deltaT){
		// gen id
		final String marker_id = boneName + "_" + this.randString(4);
		// create the marker
		this.ViewBoneTraj(actorName, boneName, start, end, marker_id, markerType, color, scale, deltaT);
	}
	
	/**
	 * View and return the Pose of the actor between the given timepoints
	 */
	public void ViewBoneTraj(String actorName,
			String boneName,
			double start,
			double end,
			String markerType,
			String color,
			float scale,
			double deltaT){
		// gen id
		final String marker_id = boneName + "_" + this.randString(4);
		// create the marker
		this.ViewBoneTraj(actorName, boneName, start, end, marker_id, markerType, color, scale, deltaT);
	}
	
	/**
	 * View and return the Pose of the actor between the given timepoints
	 */
	public void ViewBoneTraj(String actorName,
			String boneName,
			String start,
			String end,
			String markerID,
			String markerType,
			String color,
			float scale,
			double deltaT){
		// get the trajectory
		final double[][] traj = this.GetBoneTraj(actorName, boneName, start, end, deltaT);
		
		// positions as arraylist
		ArrayList<Vector3d> pos = new ArrayList<Vector3d>();
		
		// add trajectory points
		for (int i = 0; i < traj.length; i++){
			pos.add(new Vector3d(traj[i][0], traj[i][1], traj[i][2]));
		}	
	
		// create the markers
		this.CreateMarkers(pos, markerID, markerType, color, scale);
	}
	
	/**
	 * View and return the Pose of the actor between the given timepoints
	 */
	public void ViewBoneTraj(String actorName,
			String boneName,
			double start,
			double end,
			String markerID,
			String markerType,
			String color,
			float scale,
			double deltaT){
		// get the trajectory
		final double[][] traj = this.GetBoneTraj(actorName, boneName, start, end, deltaT);
		
		// positions as arraylist
		ArrayList<Vector3d> pos = new ArrayList<Vector3d>();
		
		// add trajectory points
		for (int i = 0; i < traj.length; i++){
			pos.add(new Vector3d(traj[i][0], traj[i][1], traj[i][2]));
		}	
	
		// create the markers
		this.CreateMarkers(pos, markerID, markerType, color, scale);
	}

	/**
	 * View and return the Poses of the actor bones at the given timepoint (or the most recent one)
	 */
	public void ViewBonesPoses(String actorName,
			String timestampStr,
			String markerType,
			String color,
			float scale){
		// gen id
		final String marker_id = actorName + "_" + this.randString(4);
		// create the marker
		this.ViewBonesPoses(actorName, timestampStr, marker_id, markerType, color, scale);
	}
	
	/**
	 * View and return the Poses of the actor bones at the given timepoint (or the most recent one)
	 */
	public void ViewBonesPoses(String actorName,
			double timestamp,
			String markerType,
			String color,
			float scale){
		// gen id
		final String marker_id = actorName + "_" + this.randString(4);
		// create the marker
		this.ViewBonesPoses(actorName, timestamp, marker_id, markerType, color, scale);
	}

	/**
	 * View and return the Poses of the actor bones at the given timepoint (or the most recent one)
	 */
	public void ViewBonesPoses(String actorName,
			String timestampStr,
			String markerID,
			String markerType,
			String color,
			float scale){
		// get the bones poses
		final double[][] poses = this.GetBonesPosesAt(actorName, timestampStr);
		
		// positions as arraylist
		ArrayList<Vector3d> pos = new ArrayList<Vector3d>();
		
		// add bones points
		for (int i = 0; i < poses.length; i++){
			pos.add(new Vector3d(poses[i][0], poses[i][1], poses[i][2]));
		}
	
		// create the markers
		this.CreateMarkers(pos, markerID, markerType, color, scale);
	}
	
	/**
	 * View and return the Poses of the actor bones at the given timepoint (or the most recent one)
	 */
	public void ViewBonesPoses(String actorName,
			double timestamp,
			String markerID,
			String markerType,
			String color,
			float scale){
		// get the bones poses
		final double[][] poses = this.GetBonesPosesAt(actorName, timestamp);
		
		// positions as arraylist
		ArrayList<Vector3d> pos = new ArrayList<Vector3d>();
		
		// add bones points
		for (int i = 0; i < poses.length; i++){
			pos.add(new Vector3d(poses[i][0], poses[i][1], poses[i][2]));
		}
	
		// create the markers
		this.CreateMarkers(pos, markerID, markerType, color, scale);
	}

	/**
	 * View and return the Trajectories of the actor bones at the given timepoint (or the most recent one)
	 */
	public void ViewBonesTrajs(String actorName,
			String start,
			String end,
			String markerType,
			String color,
			float scale,
			double deltaT){
		// gen id
		final String marker_id = actorName + "_" + this.randString(4);
		// create the marker
		this.ViewBonesTrajs(actorName, start, end, marker_id, markerType, color, scale, deltaT);
	}
	
	/**
	 * View and return the Trajectories of the actor bones at the given timepoint (or the most recent one)
	 */
	public void ViewBonesTrajs(String actorName,
			double start,
			double end,
			String markerType,
			String color,
			float scale,
			double deltaT){
		// gen id
		final String marker_id = actorName + "_" + this.randString(4);
		// create the marker
		this.ViewBonesTrajs(actorName, start, end, marker_id, markerType, color, scale, deltaT);
	}
	
	/**
	 * View and return the Trajectories of the actor bones at the given timepoint (or the most recent one)
	 */
	public void ViewBonesTrajs(String actorName,
			String start,
			String end,
			String markerID,
			String markerType,
			String color,
			float scale,
			double deltaT){
		// call further using double for timestamps
		final double[][][] trajs = this.GetBonesTrajs(actorName, start, end, deltaT);
		
		// positions as arraylist
		ArrayList<Vector3d> pos = new ArrayList<Vector3d>();
		
		// add trajectory points to the marker
		for (int i = 0; i < trajs.length; i++){
			for(int j = 0; j < trajs[i].length; j++){				
				pos.add(new Vector3d(trajs[i][j][0], trajs[i][j][1], trajs[i][j][2]));
			}
		}
		
		// create the markers
		this.CreateMarkers(pos, markerID, markerType, color, scale);
	}
	
	/**
	 * View and return the Trajectories of the actor bones at the given timepoint (or the most recent one)
	 */
	public void ViewBonesTrajs(String actorName,
			double start,
			double end,
			String markerID,
			String markerType,
			String color,
			float scale,
			double deltaT){
		// call further using double for timestamps
		final double[][][] trajs = this.GetBonesTrajs(actorName, start, end, deltaT);
		
		// positions as arraylist
		ArrayList<Vector3d> pos = new ArrayList<Vector3d>();
		
		// add trajectory points to the marker
		for (int i = 0; i < trajs.length; i++){
			for(int j = 0; j < trajs[i].length; j++){				
				pos.add(new Vector3d(trajs[i][j][0], trajs[i][j][1], trajs[i][j][2]));
			}
		}
		
		// create the markers
		this.CreateMarkers(pos, markerID, markerType, color, scale);
	}

	////////////////////////////////////////////////////////////////
	///// ADD RATING
	public void AddRating(String RatingInst,
			String RatingType,
			String Score,
			String FilePath){
		// split strings from namspace / add unique hash
		final String rating_inst = RatingInst.split("#")[1];
		final String rating_type = RatingType.split("#")[1];
		final String rating_type_inst = rating_type + "_" + this.randString(4);		
		final String rating_str = 
				"\t<!-- Object: " + rating_type_inst + "-->\n" + 
				"\t<owl:NamedIndividual rdf:about=\"&log;" + rating_type_inst + "\">\n" +
				"\t\t<rdf:type rdf:resource=\"&knowrob_u;" + rating_type +"\"/>\n" +
				"\t\t<knowrob_u:ratingScore rdf:datatype=\"&xsd; float\">" + Score + "</knowrob_u:ratingScore>\n" +
				"\t\t<knowrob_u:ratingOf rdf:resource=\"&log;" + rating_inst +"\"/>\n" +
				"\t</owl:NamedIndividual>";

		// append rating to file
		try {
			// remove last line of the file
			RandomAccessFile randomAccessFile = new RandomAccessFile(FilePath, "rw");
			byte b;
			long length = randomAccessFile.length() ;
			if (length != 0) {
				do {
					length -= 1;
					randomAccessFile.seek(length);
					b = randomAccessFile.readByte();
				} while (b != 10 && length > 0);
				randomAccessFile.setLength(length);
				randomAccessFile.close();
			}		
			// append rating string to file
			PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(FilePath, true)));
			out.println(rating_str);
			out.print("\n</rdf:RDF>");
			out.close();
		} catch (IOException e) {
		}
	}

    /*****************************************/
    /*                 EEG                   */
    /*****************************************/

    /**
     * Query the EEG value at the given timepoint (or the most recent one)
     */ 
    public double GetEEGValueAt(int channelNr,  String timestampStr){
        // transform the knowrob time to double with 3 decimal precision
        final double timestamp = (double) Math.round(parseTime_d(timestampStr) * 1000) / 1000;

        return GetEEGValueAt(channelNr, timestamp);
    }

    public double GetEEGValueAt(int channelNr, double timestamp){
        // EEG channel name
        final String eeg_channel_path = "eeg.c" + Integer.toString(channelNr);

        // $and list for querying the $match in the aggregation
        BasicDBList time_and_channel = new BasicDBList();

        // add the timestamp and the actor name
        time_and_channel.add(new BasicDBObject(eeg_channel_path, new BasicDBObject("$exists", true)));
        time_and_channel.add(new BasicDBObject("timestamp", new BasicDBObject("$lte", timestamp)));

        // create the pipeline operations, first the $match
        DBObject match_time_and_channel = new BasicDBObject(
                "$match", new BasicDBObject( "$and", time_and_channel)); 

                // sort the results in descending order on the timestamp (keep most recent result first)
        DBObject sort_desc = new BasicDBObject(
                "$sort", new BasicDBObject("timestamp", -1));

        // $limit the result to 1, we only need one pose
        DBObject limit_result = new BasicDBObject("$limit", 1);

        // build the $projection operation
        DBObject proj_fields = new BasicDBObject("_id", 0);
        proj_fields.put("timestamp", 1);
        proj_fields.put("eeg", "$" + eeg_channel_path);        
        DBObject project = new BasicDBObject("$project", proj_fields);

        // run aggregation
        List<DBObject> pipeline = Arrays.asList(match_time_and_channel, sort_desc, limit_result, project);
        
        AggregationOptions aggregationOptions = AggregationOptions.builder()
                .batchSize(100)
                .outputMode(AggregationOptions.OutputMode.CURSOR)
                .allowDiskUse(true)
                .build();

        // get results
        Cursor cursor = this.MongoRobcogConn.coll.aggregate(pipeline, aggregationOptions);

        // if query has a response, return the pose
        if(cursor.hasNext())
        {
            // get the first document as the next cursor and append the metadata to it
            BasicDBObject first_doc = (BasicDBObject) cursor.next();            
            // close cursor
            cursor.close();
            // get the pose
            return first_doc.getDouble("eeg");
        }
        else
        {
            System.out.println("Java - GetEEGValueAt - No results found, returning empty list..");           
            return 0;
        }
    }


    /**
     * Query the values of the EEG channel between the timestamps
     */
    public double[] GetEEGValues(int channelNr,
            String start,
            String end,
            double deltaT){
        // transform the knowrob time to double with 3 decimal precision
        final double start_ts = (double) Math.round(parseTime_d(start) * 1000) / 1000;
        final double end_ts = (double) Math.round(parseTime_d(end) * 1000) / 1000;
        
        // EEG channel name
        final String eeg_channel_path = "eeg.c" + Integer.toString(channelNr);

        // $and list for querying the $match in the aggregation
        BasicDBList time_and_channel = new BasicDBList();

        // add the timestamp and the actor name
        time_and_channel.add(new BasicDBObject(eeg_channel_path, new BasicDBObject("$exists", true)));
        time_and_channel.add(new BasicDBObject("timestamp", new BasicDBObject("$gte", start_ts).append("$lte", end_ts)));

        // create the pipeline operations, first the $match
        DBObject match_time_and_channel = new BasicDBObject(
                "$match", new BasicDBObject( "$and", time_and_channel)); 

                // sort the results in descending order on the timestamp (keep most recent result first)
        DBObject sort_inc = new BasicDBObject(
                "$sort", new BasicDBObject("timestamp", 1));


        // build the $projection operation
        DBObject proj_fields = new BasicDBObject("_id", 0);
        proj_fields.put("timestamp", 1);
        proj_fields.put("eeg", "$" + eeg_channel_path);        
        DBObject project = new BasicDBObject("$project", proj_fields);

        // run aggregation
        List<DBObject> pipeline = Arrays.asList(match_time_and_channel, sort_inc, project);

        AggregationOptions aggregationOptions = AggregationOptions.builder()
                .batchSize(100)
                .outputMode(AggregationOptions.OutputMode.CURSOR)
                .allowDiskUse(true)
                .build();

        // get results
        Cursor cursor = this.MongoRobcogConn.coll.aggregate(pipeline, aggregationOptions);
        
        // Traj as dynamic array
        ArrayList<Double> traj_list = new ArrayList<Double>();
        
        // if the query returned nothing, get the most recent pose
        if(!cursor.hasNext())
        {
            System.out.println("Java - GetEEGValues - No results found, returning most recent pose..");
            // get the most recent pose
            traj_list.add(this.GetEEGValueAt(channelNr, start));
            
            // cast from dynamic array to standard array
            // java does not support this, has to be Double (as class)
            //return traj_list.toArray(new double[traj_list.size()]);  
            double[] traj_arr = new double[traj_list.size()];
            for (int i = 0; i < traj_list.size(); i++) {
                traj_arr[i] = traj_list.get(i);                // java 1.5+ style (outboxing)
            }
            return traj_arr;
        }
        
        // timestamp used for deltaT
        double prev_ts = 0;
                
        // while query has a response, return the pose
        while(cursor.hasNext())
        {
            // get the first document as the next cursor and append the metadata to it
            BasicDBObject curr_doc = (BasicDBObject) cursor.next();
            
            // get the curr timestamp
            double curr_ts = curr_doc.getDouble("timestamp");
            
            // if time diff > then deltaT add position to trajectory
            if(curr_ts - prev_ts > deltaT)
            {           
                // get the current pose
                traj_list.add(curr_doc.getDouble("eeg"));
                prev_ts = curr_ts;
                //System.out.println(curr_doc.toString());
            }
        }
        // close cursor
        cursor.close();     
        
        // cast from dynamic array to standard array
        // java does not support this, has to be Double (as class)
        //return traj_list.toArray(new double[traj_list.size()]);  
        double[] traj_arr = new double[traj_list.size()];
        for (int i = 0; i < traj_list.size(); i++) {
            traj_arr[i] = traj_list.get(i);                // java 1.5+ style (outboxing)
        }
        return traj_arr;
    }

	// ======================== ALL EEG Channels ============================

    /**
     * Query the all channel EEG values at the given timepoint (or the most recent one)
     */ 
    public double[] GetAllEEGValuesAt(String timestampStr){
        // transform the knowrob time to double with 3 decimal precision
        final double timestamp = (double) Math.round(parseTime_d(timestampStr) * 1000) / 1000;

        return GetAllEEGValuesAt(timestamp);
    }

    public double[] GetAllEEGValuesAt(double timestamp){
        // $and list for querying the $match in the aggregation
        BasicDBList time_and_channel = new BasicDBList();

        // add the timestamp and the actor name
        time_and_channel.add(new BasicDBObject("eeg", new BasicDBObject("$exists", true)));
        time_and_channel.add(new BasicDBObject("timestamp", new BasicDBObject("$lte", timestamp)));

        // create the pipeline operations, first the $match
        DBObject match_time_and_channel = new BasicDBObject(
                "$match", new BasicDBObject( "$and", time_and_channel)); 

                // sort the results in descending order on the timestamp (keep most recent result first)
        DBObject sort_desc = new BasicDBObject(
                "$sort", new BasicDBObject("timestamp", -1));

        // $limit the result to 1, we only need one pose
        DBObject limit_result = new BasicDBObject("$limit", 1);

        // build the $projection operation
        DBObject proj_fields = new BasicDBObject("_id", 0);
        proj_fields.put("timestamp", 1);
        proj_fields.put("eeg", "$eeg");        
        DBObject project = new BasicDBObject("$project", proj_fields);

        // run aggregation
        List<DBObject> pipeline = Arrays.asList(match_time_and_channel, sort_desc, limit_result, project);
        
        AggregationOptions aggregationOptions = AggregationOptions.builder()
                .batchSize(100)
                .outputMode(AggregationOptions.OutputMode.CURSOR)
                .allowDiskUse(true)
                .build();

        // get results
        Cursor cursor = this.MongoRobcogConn.coll.aggregate(pipeline, aggregationOptions);

		// if query has a response, return the pose
		if(cursor.hasNext())
		{
			// get the first document as the next cursor and append the metadata to it
			BasicDBObject first_doc = (BasicDBObject) cursor.next();			
			// close cursor
			cursor.close();

			// get all the channels // TODO hardcoded
			return new double[] {
					((BasicDBObject) first_doc.get("eeg")).getDouble("c1"),
					((BasicDBObject) first_doc.get("eeg")).getDouble("c2"),
					((BasicDBObject) first_doc.get("eeg")).getDouble("c3"),
					((BasicDBObject) first_doc.get("eeg")).getDouble("c4"),
					((BasicDBObject) first_doc.get("eeg")).getDouble("c5"),
					((BasicDBObject) first_doc.get("eeg")).getDouble("c6"),
					((BasicDBObject) first_doc.get("eeg")).getDouble("c7"),
					((BasicDBObject) first_doc.get("eeg")).getDouble("c8"),
					((BasicDBObject) first_doc.get("eeg")).getDouble("c9"),
					((BasicDBObject) first_doc.get("eeg")).getDouble("c10"),
					((BasicDBObject) first_doc.get("eeg")).getDouble("c11"),
					((BasicDBObject) first_doc.get("eeg")).getDouble("c12"),
					((BasicDBObject) first_doc.get("eeg")).getDouble("c13"),
					((BasicDBObject) first_doc.get("eeg")).getDouble("c14")};
		}
		else
		{
			System.out.println("Java - GetAllEEGValuesAt - No results found, returning empty list..");			
			return new double[0];
		}
    }


    /**
     * Query the values of all the EEG channels between the timestamps
     */
    public double[][][] GetAllEEGValues(String start,
            String end,
            double deltaT){
        // transform the knowrob time to double with 3 decimal precision
        final double start_ts = (double) Math.round(parseTime_d(start) * 1000) / 1000;
        final double end_ts = (double) Math.round(parseTime_d(end) * 1000) / 1000;

        // $and list for querying the $match in the aggregation
        BasicDBList time_and_channel = new BasicDBList();

        // add the timestamp and the actor name
        time_and_channel.add(new BasicDBObject("eeg", new BasicDBObject("$exists", true)));
        time_and_channel.add(new BasicDBObject("timestamp", new BasicDBObject("$gte", start_ts).append("$lte", end_ts)));

        // create the pipeline operations, first the $match
        DBObject match_time_and_channel = new BasicDBObject(
                "$match", new BasicDBObject( "$and", time_and_channel)); 

                // sort the results in descending order on the timestamp (keep most recent result first)
        DBObject sort_inc = new BasicDBObject(
                "$sort", new BasicDBObject("timestamp", 1));


        // build the $projection operation
        DBObject proj_fields = new BasicDBObject("_id", 0);
        proj_fields.put("timestamp", 1);
        proj_fields.put("eeg", "$eeg");        
        DBObject project = new BasicDBObject("$project", proj_fields);

        // run aggregation
        List<DBObject> pipeline = Arrays.asList(match_time_and_channel, sort_inc, project);

        AggregationOptions aggregationOptions = AggregationOptions.builder()
                .batchSize(100)
                .outputMode(AggregationOptions.OutputMode.CURSOR)
                .allowDiskUse(true)
                .build();

        // get results
        Cursor cursor = this.MongoRobcogConn.coll.aggregate(pipeline, aggregationOptions);
        
		// Traj as dynamic array
		ArrayList<double[][]> eeg_channels_values = new ArrayList<double[][]>();

		ArrayList<double[]> timestap_values_list = new ArrayList<double[]>();
		
		// if the query returned nothing, get the most recent pose
		if(!cursor.hasNext())
		{
			System.out.println("Java - GetAllEEGValues - No results found, returning most recent pose..");
			// get the most recent pose
			//eeg_channels_values.add(this.GetAllEEGValuesAt(start));
			
			// cast from dynamic array to standard array
			//return eeg_channels_values.toArray(new double[eeg_channels_values.size()][2][14]);
			return new double[0][0][0];
		}
		
		// timestamp used for deltaT
		double prev_ts = 0;
		
		ArrayList<Double> c1 = new ArrayList<Double>();
		ArrayList<Double> c2 = new ArrayList<Double>();
		ArrayList<Double> c3 = new ArrayList<Double>();
		ArrayList<Double> c4 = new ArrayList<Double>();
		ArrayList<Double> c5 = new ArrayList<Double>();
		ArrayList<Double> c6 = new ArrayList<Double>();
		ArrayList<Double> c7 = new ArrayList<Double>();
		ArrayList<Double> c8 = new ArrayList<Double>();
		ArrayList<Double> c9 = new ArrayList<Double>();
		ArrayList<Double> c10 = new ArrayList<Double>();
		ArrayList<Double> c11 = new ArrayList<Double>();
		ArrayList<Double> c12 = new ArrayList<Double>();
		ArrayList<Double> c13 = new ArrayList<Double>();
		ArrayList<Double> c14 = new ArrayList<Double>();
		
		// while query has a response, return the pose
		while(cursor.hasNext())
		{
			// get the first document as the next cursor and append the metadata to it
			BasicDBObject curr_doc = (BasicDBObject) cursor.next();
			
			// get the curr timestamp
			double curr_ts = curr_doc.getDouble("timestamp");
			
			// if time diff > then deltaT add position to trajectory
			if(curr_ts - prev_ts > deltaT)
			{				
				c1.add(((BasicDBObject) curr_doc.get("eeg")).getDouble("c1"));
				c2.add(((BasicDBObject) curr_doc.get("eeg")).getDouble("c2"));
				c3.add(((BasicDBObject) curr_doc.get("eeg")).getDouble("c3"));
				c4.add(((BasicDBObject) curr_doc.get("eeg")).getDouble("c4"));
				c5.add(((BasicDBObject) curr_doc.get("eeg")).getDouble("c5"));
				c6.add(((BasicDBObject) curr_doc.get("eeg")).getDouble("c6"));
				c7.add(((BasicDBObject) curr_doc.get("eeg")).getDouble("c7"));
				c8.add(((BasicDBObject) curr_doc.get("eeg")).getDouble("c8"));
				c9.add(((BasicDBObject) curr_doc.get("eeg")).getDouble("c9"));
				c10.add(((BasicDBObject) curr_doc.get("eeg")).getDouble("c10"));
				c11.add(((BasicDBObject) curr_doc.get("eeg")).getDouble("c11"));
				c12.add(((BasicDBObject) curr_doc.get("eeg")).getDouble("c12"));
				c13.add(((BasicDBObject) curr_doc.get("eeg")).getDouble("c13"));
				c14.add(((BasicDBObject) curr_doc.get("eeg")).getDouble("c14"));

				prev_ts = curr_ts;
			}
		}

		double[] ts_arr = new double[]{start_ts, end_ts, deltaT};

        // cast from dynamic array to standard array
        // java does not support this, has to be Double (as class)
        //return traj_list.toArray(new double[traj_list.size()]);  
        double[] c1_arr = new double[c1.size()];
        double[] c2_arr = new double[c2.size()];
        double[] c3_arr = new double[c3.size()];
        double[] c4_arr = new double[c4.size()];
        double[] c5_arr = new double[c5.size()];
        double[] c6_arr = new double[c6.size()];
        double[] c7_arr = new double[c7.size()];
        double[] c8_arr = new double[c8.size()];
        double[] c9_arr = new double[c9.size()];
        double[] c10_arr = new double[c10.size()];
        double[] c11_arr = new double[c11.size()];
        double[] c12_arr = new double[c12.size()];
        double[] c13_arr = new double[c13.size()];
        double[] c14_arr = new double[c14.size()];

        for (int i = 0; i < c1.size(); i++) {
            c1_arr[i] = c1.get(i);                // java 1.5+ style (outboxing)
			c2_arr[i] = c2.get(i);
			c3_arr[i] = c3.get(i); 
			c4_arr[i] = c4.get(i); 
			c5_arr[i] = c5.get(i); 
			c6_arr[i] = c6.get(i); 
			c7_arr[i] = c7.get(i); 
			c8_arr[i] = c8.get(i); 
			c9_arr[i] = c9.get(i); 
			c10_arr[i] = c10.get(i); 
			c11_arr[i] = c11.get(i); 
			c12_arr[i] = c12.get(i); 
			c13_arr[i] = c13.get(i); 
			c14_arr[i] = c14.get(i); 
        }
		
		timestap_values_list.add(ts_arr);
		timestap_values_list.add(c1_arr);
		eeg_channels_values.add(timestap_values_list.toArray(new double[2][c1_arr.length]));
		timestap_values_list.clear();

		timestap_values_list.add(ts_arr);
		timestap_values_list.add(c2_arr);
		eeg_channels_values.add(timestap_values_list.toArray(new double[2][c2_arr.length]));
		timestap_values_list.clear();

		timestap_values_list.add(ts_arr);
		timestap_values_list.add(c3_arr);
		eeg_channels_values.add(timestap_values_list.toArray(new double[2][c3_arr.length]));
		timestap_values_list.clear();

		timestap_values_list.add(ts_arr);
		timestap_values_list.add(c4_arr);
		eeg_channels_values.add(timestap_values_list.toArray(new double[2][c4_arr.length]));
		timestap_values_list.clear();

		timestap_values_list.add(ts_arr);
		timestap_values_list.add(c5_arr);
		eeg_channels_values.add(timestap_values_list.toArray(new double[2][c5_arr.length]));
		timestap_values_list.clear();

		timestap_values_list.add(ts_arr);
		timestap_values_list.add(c6_arr);
		eeg_channels_values.add(timestap_values_list.toArray(new double[2][c6_arr.length]));
		timestap_values_list.clear();

		timestap_values_list.add(ts_arr);
		timestap_values_list.add(c7_arr);
		eeg_channels_values.add(timestap_values_list.toArray(new double[2][c7_arr.length]));
		timestap_values_list.clear();

		timestap_values_list.add(ts_arr);
		timestap_values_list.add(c8_arr);
		eeg_channels_values.add(timestap_values_list.toArray(new double[2][c8_arr.length]));
		timestap_values_list.clear();

		timestap_values_list.add(ts_arr);
		timestap_values_list.add(c9_arr);
		eeg_channels_values.add(timestap_values_list.toArray(new double[2][c9_arr.length]));
		timestap_values_list.clear();

		timestap_values_list.add(ts_arr);
		timestap_values_list.add(c10_arr);
		eeg_channels_values.add(timestap_values_list.toArray(new double[2][c10_arr.length]));
		timestap_values_list.clear();

		timestap_values_list.add(ts_arr);
		timestap_values_list.add(c11_arr);
		eeg_channels_values.add(timestap_values_list.toArray(new double[2][c11_arr.length]));
		timestap_values_list.clear();

		timestap_values_list.add(ts_arr);
		timestap_values_list.add(c12_arr);
		eeg_channels_values.add(timestap_values_list.toArray(new double[2][c12_arr.length]));
		timestap_values_list.clear();

		timestap_values_list.add(ts_arr);
		timestap_values_list.add(c13_arr);
		eeg_channels_values.add(timestap_values_list.toArray(new double[2][c13_arr.length]));
		timestap_values_list.clear();

		timestap_values_list.add(ts_arr);
		timestap_values_list.add(c14_arr);
		eeg_channels_values.add(timestap_values_list.toArray(new double[2][c14_arr.length]));
		timestap_values_list.clear();
		// close cursor
		cursor.close();		
		
		// cast from dynamic array to standard array
		return eeg_channels_values.toArray(
			new double[eeg_channels_values.size()][timestap_values_list.size()][c1_arr.length]);
	}
}

