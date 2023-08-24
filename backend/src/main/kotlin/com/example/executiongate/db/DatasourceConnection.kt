package com.example.executiongate.db

import com.example.executiongate.db.util.BaseEntity
import com.example.executiongate.db.util.ReviewConfigConverter
import com.example.executiongate.service.EntityNotFound
import com.example.executiongate.service.dto.AuthenticationType
import com.example.executiongate.service.dto.Datasource
import com.example.executiongate.service.dto.DatasourceConnection
import com.example.executiongate.service.dto.DatasourceConnectionId
import jakarta.persistence.CascadeType
import org.apache.commons.lang3.builder.ToStringBuilder
import org.apache.commons.lang3.builder.ToStringStyle.SHORT_PREFIX_STYLE
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import jakarta.persistence.Column
import jakarta.persistence.Convert
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.OneToMany
import org.hibernate.annotations.ColumnTransformer


data class ReviewConfig(
        val numTotalRequired: Int,
)


@Entity(name = "datasource_connection")
class DatasourceConnectionEntity(
        @ManyToOne(cascade = [CascadeType.ALL])
        @JoinColumn(name = "datasource_id")
        val datasource: DatasourceEntity,
        var displayName: String,
        @Enumerated(EnumType.STRING)
        var authenticationType: AuthenticationType,
        var username: String,
        var password: String,
        var description: String,
        @Column(columnDefinition = "json")
        @Convert(converter = ReviewConfigConverter::class)
        @ColumnTransformer(write = "?::json")
        var reviewConfig: ReviewConfig,
        @OneToMany(mappedBy = "connection", cascade = [CascadeType.ALL])
        val executionRequests: Set<ExecutionRequestEntity> = emptySet(),
) : BaseEntity() {

    override fun toString(): String = ToStringBuilder(this, SHORT_PREFIX_STYLE)
            .append("id", id)
            .append("name", displayName)
            .toString()

    fun toDto(datasourceDto: Datasource? = null) = DatasourceConnection(
            id = DatasourceConnectionId(id),
            datasource = datasourceDto ?: datasource.toDto(),
            displayName = displayName,
            authenticationType = authenticationType,
            username = username,
            password = password,
            description = description,
            reviewConfig = reviewConfig,

            )

}

interface DatasourceConnectionRepository : JpaRepository<DatasourceConnectionEntity, String>

@Service
class DatasourceConnectionAdapter(
        val datasourceConnectionRepository: DatasourceConnectionRepository
) {

    fun getDatasourceConnection(id: DatasourceConnectionId): DatasourceConnection =
            datasourceConnectionRepository.findByIdOrNull(id.toString())?.toDto()
                    ?: throw EntityNotFound("Datasource Connection Not Found", "Datasource Connection with id $id does not exist.")

    fun updateDatasourceConnection(
            id: DatasourceConnectionId,
            displayName: String,
            username: String,
            password: String,
            description: String,
            reviewConfig: ReviewConfig,
    ): DatasourceConnection {
        val datasourceConnection = datasourceConnectionRepository.findByIdOrNull(id.toString())
                ?: throw EntityNotFound("Datasource Connection Not Found", "Datasource Connection with id $id does not exist.")
        datasourceConnection.displayName = displayName
        datasourceConnection.username = username
        datasourceConnection.password = password
        datasourceConnection.description = description
        datasourceConnection.reviewConfig = reviewConfig

        return datasourceConnectionRepository.save(datasourceConnection).toDto()
    }
}