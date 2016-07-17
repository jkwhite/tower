/*
    Tower
    Copyright (C) 2007, John K White, All Rights Reserved
*/
/*
    This program is free software; you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation; either version 2 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program; if not, write to the Free Software
    Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.
*/
package org.excelsi.aether.ui;


import java.net.URL;
import java.util.logging.Logger;
import com.jmex.audio.*;
import com.jmex.audio.AudioTrack.TrackType;
import java.util.*;
import org.excelsi.aether.*;


public class Audio {
    private static Audio _audio;
    private AudioSystem _a;
    private String _prefix;
    private boolean _disabled = Boolean.getBoolean("tower.nosound");
    private Map<String,AudioTrack> _tracks = new HashMap<String,AudioTrack>();


    private Audio() {
        _a = AudioSystem.getSystem();
        _prefix = System.getProperty("tower.audio", "default");
    }

    public void play(NHBot source, String track) {
        if(source==null||_disabled) {
            return;
        }
        NHBot p = player();
        float dist = source.isPlayer()?0f:3f;
        if(source.getEnvironment()!=null) {
            dist = source.getEnvironment().getMSpace().distance(p.getEnvironment().getMSpace());
        }
        if(!p.getEnvironment().getVisibleBots().contains(source)) {
            dist += dist*0.8f;
        }
        play(dist, track);
    }

    public void play(NHSpace source, String track) {
        if(_disabled) {
            return;
        }
        NHBot p = player();
        float dist = source.distance(p.getEnvironment().getMSpace());
        if(!p.getEnvironment().getVisible().contains(source)) {
            dist += dist*0.8f;
        }
        play(dist, track);
    }

    private NHBot player() {
        return Universe.getUniverse().getGame().getPlayer();
    }

    private void play(float dist, String track) {
        AudioTrack t = _tracks.get(track);
        if(t==null) {
            URL u = Thread.currentThread().getContextClassLoader().getResource("audio/"+_prefix+"/"+track+".wav");
            if(u==null) {
                Logger.global.warning("no audio for '"+track+"'");
                return;
            }
            t = _a.createAudioTrack(u, false);
            t.setLooping(false);
            t.setRelative(false);
            t.setType(TrackType.POSITIONAL);
            _tracks.put(track, t);
        }
        float vol = 2f*Math.max(0f, 8f-dist)/8f;
        if(vol>0f) {
            // for some reason a vol of 1.0f is silent
            //t.setTargetVolume(Math.min(0.9f, vol));
            t.setTargetVolume(vol);
            t.play();
        }
    }

    public static Audio getAudio() {
        if(_audio==null) {
            _audio = new Audio();
        }
        return _audio;
    }
}
