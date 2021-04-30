package model;

import java.util.HashMap;
import java.util.Map;

public class PoolConexoes {
	
	private static Map<Integer, ThreadConexao> poolConexoes = new HashMap<Integer, ThreadConexao>();
	
	private PoolConexoes(){
		
	}
	
	public static void addConexao(int idVeiculo,ThreadConexao th){
		synchronized(poolConexoes){
			if(poolConexoes.containsKey(idVeiculo)){
				poolConexoes.remove(idVeiculo);
			}
			poolConexoes.put(idVeiculo, th);
		}
	}
	
	public static void removerConexao(int idVeiculo){
		synchronized(poolConexoes){
			if(poolConexoes.containsKey(idVeiculo)){
				poolConexoes.remove(idVeiculo);
			}
		}
	}
	
	public static ThreadConexao getConexao(int idVeiculo){
		synchronized(poolConexoes){
			if(poolConexoes.containsKey(idVeiculo)){
				return poolConexoes.get(idVeiculo);
			}else{
				return null;
			}
		}
	}


}
