package com.ws.mesh.awe.db;

import com.we_smart.sqldao.BaseDAO;
import com.ws.mesh.awe.bean.Mesh;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MeshDAO extends BaseDAO<Mesh> {

    private MeshDAO() {
        super(Mesh.class);
    }

    private static MeshDAO meshDAO;

    public static MeshDAO getInstance() {
        if (meshDAO == null) {
            synchronized (MeshDAO.class) {
                if (meshDAO == null) {
                    meshDAO = new MeshDAO();
                }
            }
        }
        return meshDAO;
    }

    public boolean insertMesh(Mesh mesh){
        return insert(mesh);
    }

    public boolean deleteMesh(Mesh mesh){
        return delete(mesh, mesh.mMeshName);
    }

    public Map<String, Mesh> queryMeshes(){
        List<Mesh> meshes = query(null);
        Map<String, Mesh> meshMap = new HashMap<>();
        for (Mesh mesh : meshes) {
            meshMap.put(mesh.mMeshName, mesh);
        }
        return meshMap;
    }

    public boolean updateMesh(Mesh mesh){
        return update(mesh, "mMeshName");
    }
}
