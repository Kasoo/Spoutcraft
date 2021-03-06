/*
 * This file is part of Spoutcraft (http://www.spout.org/).
 *
 * Spoutcraft is licensed under the SpoutDev License Version 1.
 *
 * Spoutcraft is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * In addition, 180 days after any changes are published, you can use the
 * software, incorporating those changes, under the terms of the MIT license,
 * as described in the SpoutDev License Version 1.
 *
 * Spoutcraft is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License,
 * the MIT license and the SpoutDev license version 1 along with this program.
 * If not, see <http://www.gnu.org/licenses/> for the GNU Lesser General Public
 * License and see <http://www.spout.org/SpoutDevLicenseV1.txt> for the full license,
 * including the MIT license.
 */
package org.spoutcraft.client.entity;

import java.util.HashMap;

public class EntityData {
	private HashMap<Byte, String> customTextures = new HashMap<Byte, String>();
	private byte textureToRender = 0;
	private double gravityMod = 1D;
	private double walkingMod = 1D;
	private double swimmingMod = 1D;
	private double jumpingMod = 1D;
	private double airspeedMod = 1D;

	public byte getTextureToRender() {
		return textureToRender;
	}

	public void setTextureToRender(byte textureToRender) {
		this.textureToRender = textureToRender;
	}

	public HashMap<Byte, String> getCustomTextures() {
		return customTextures;
	}

	public double getWalkingMod() {
		return walkingMod;
	}

	public void setWalkingMod(double walkingMod) {
		this.walkingMod = walkingMod;
	}

	public double getGravityMod() {
		return gravityMod;
	}

	public void setGravityMod(double gravityMod) {
		this.gravityMod = gravityMod;
	}

	public double getSwimmingMod() {
		return swimmingMod;
	}

	public void setSwimmingMod(double swimmingMod) {
		this.swimmingMod = swimmingMod;
	}

	public double getJumpingMod() {
		return jumpingMod;
	}

	public void setJumpingMod(double jumpingMod) {
		this.jumpingMod = jumpingMod;
	}

	public double getAirspeedMod() {
		return airspeedMod;
	}

	public void setAirspeedMod(double airspeedMod) {
		this.airspeedMod = airspeedMod;
	}
}
